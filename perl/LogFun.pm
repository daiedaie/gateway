#!/usr/bin/perl -w
#
#@defgroup   日志模块
#@author     gary
#@version    1.0
#@date       20101027

#公共函数：
#
#设置
#SetLogLevel(loglevel, termlevel);
#SetLogPath(path);
#SetLogHead(filehead,  contenthead);
#SetLogSize(size);
#
#OpenLog();
#CloseLog();
#
#WriteLog(level, msg);
#WriteTrace(msg);
#WriteDebug(msg);
#WriteInfo(msg);
#WriteWarn(msg);
#WriteError(msg);
#WriteFatal(msg);
#WriteClosure(msg);
#
#ps:  level  = ('T', 'D', 'I', 'W', 'E', 'F', 'C');   defaul is  'D'
#
#


use strict;
use POSIX;
use Time::HiRes qw(gettimeofday);                             #精确到微秒
use IO::Handle;    #autoflush

####错误码####
use constant LG_SET_LOG_LEVEL_ERROR                     =>  -20101;     #设置日志级别失败
use constant LG_INVALID_PATH_ERROR                      =>  -20102;     #非有效目录
use constant LG_INVALID_LOG_HEAD_ERROR                  =>  -20103;     #非有效日志头
use constant LG_INVALID_LOG_SIZE_ERROR                  =>  -20104;     #非有效日志大小
use constant LG_OPEN_LOG_ERROR                          =>  -20105;     #打开日志失败
use constant LG_INVALID_LOG_LEVEL_ERROR                 =>  -20106;     #非有效日志级别
use constant LG_INVALID_LOG_FD_ERROR                    =>  -20107;     #非有效日志描述符
use constant LG_SWITCH_LOG_ERROR                        =>  -20108;     #切换日志文件失败

####全局变量####
our $g_szLogPath         = './';                              #日志路径
our %g_logLevelGroup     = ('T'  => 0,     #TRACE
                            'D'  => 20,    #DEBUG
                            'I'  => 40,    #INFO
                            'W'  => 60,    #WARN
                            'E'  => 80,    #ERROR
                            'F'  => 100,   #FATAL
                            'C'  => 120);  #CLOSURE/END

our $g_fileLogLevel      = 'D';                               #日志文件级别
our $g_termLogLevel      = 'D';                               #终端输出级别
our $g_szLogFileHead     = 'logV1';                           #日志文件头
our $g_szLogContentHead  = 'logV1';                           #日志内容头
our $g_nMaxLogSize       = 100000;                            #日志最大条数

our $g_nCurrCycleFileNum = 0;                                 #当前日志文件数
our $g_szTmpLogName;                                          #日志文件全路径名
our $g_szTmpLogDay       = substr(LogTime2Str(time()), 0, 8); #当前日期
our $g_nTmpLogSize       = 0;                                 #当前日志大小

our $g_LOGFD;                                                 #全局日志文件描述符



#@brief 设置日志级别
#SetLogLevel(logLevel, termLevel);
#@param [in]  $logLevel    文件日志级别     Required 
#@param [out] $termLevel   屏幕日志级别     Required
#@return  
# - -20101  设置日志级别失败
# - 0       设置成功  
sub SetLogLevel
{
    #字串或数值型 
    #文件级别   屏幕级别 
    my ($logLevel, $termLevel) = @_;
    
    if(SetLevelSub($logLevel, \$g_fileLogLevel) < 0)
    {
        print STDERR "E.[LOGFUN]SetLogLevel: Set logLevel = $logLevel failed\n";   
        return LG_SET_LOG_LEVEL_ERROR;
    } 
    if(SetLevelSub($termLevel, \$g_termLogLevel) < 0)
    {
        print STDERR "E.[LOGFUN]SetLogLevel: Set termLevel = $termLevel failed\n";   
        return LG_SET_LOG_LEVEL_ERROR;
    }
    
    print STDOUT "I.[LOGFUN]SetLogLevel: SetLogLevel, logLevel = $g_fileLogLevel, termLevel = $g_termLogLevel\n";
    return 0;
}
#设置日志级别子函数, 
#@node 不是公共函数    
sub SetLevelSub
{
    #字串或数值型   变量指针 
    my ($logLevel, $pLevelVar) = @_;
    
    if(exists($g_logLevelGroup{$logLevel}))
    {
        #字符型
        $$pLevelVar = $logLevel;    
    }       
    else
    {
        #数值型
        if($logLevel =~ /^\d+$/)
        {
            my $tmpLevel = 0;
            #取最接近的日志级别较小值
            foreach my $value (sort{$a <=> $b} values %g_logLevelGroup)
            {
                last if($logLevel < $value);
                $tmpLevel = $value;   
            }    
            #转成字符
            foreach my $key (keys %g_logLevelGroup)
            {
                if($g_logLevelGroup{$key} == $tmpLevel)
                {
                    $$pLevelVar = $key;   
                }   
            }
        } 
        else
        {
            return -1;   
        }  
    }
    
    return 0;
}

#@brief 设置日志路径
#SetLogPath(szLogPath);
#@param [in]  $szLogPath    日志路径     Required 
#@return  
# - -20102  非有效日志路径
# - 0       设置成功  
sub SetLogPath
{
    my $szLogPath = shift;
    if(!(-d $szLogPath && -w $szLogPath))
    {
        return LG_INVALID_PATH_ERROR;   
    }
    
    $g_szLogPath = $szLogPath;
    
    print STDOUT "I.[LOGFUN]SetLogPath: SetLogPath, szLogPath = $g_szLogPath\n";
    return 0;
}

#@brief 设置日志头
#SetLogHead(szFileHead, szContentHead);
#@param [in]  $szFileHead     日志文件头     Required 
#@param [in]  $szContentHead  日志内容头     Required 
#@return  
# - -20103  非有效日志路径
# - 0       设置成功  
sub SetLogHead
{
    #文件头       日志内容头
    my ($szFileHead, $szContentHead) = @_;
    if(length($szFileHead) == 0 || length($szContentHead) == 0)
    {
        return LG_INVALID_LOG_HEAD_ERROR;   
    }
    
    $g_szLogFileHead = $szFileHead;   
    $g_szLogContentHead = $szContentHead;
    
    print STDOUT "I.[LOGFUN]SetLogHead: SetLogHead, szFileHead = $g_szLogFileHead, szContentHead = $g_szLogContentHead\n";
    return 0;   
}

#@brief 设置日志大小
#SetLogSize(size);
#@param [in]  $size     日志大小0<int($size)<10000000    Required 
#@return  
# - -20104  非有效日志大小
# - 0       设置成功  
sub SetLogSize
{
    my $size = shift;
    
    if(int($size) <= 0 || int($size) > 10000000)
    {
        return LG_INVALID_LOG_SIZE_ERROR;
    }
    
    $g_nMaxLogSize = int($size);   
    
    print STDOUT "I.[LOGFUN]SetLogSize: SetLogSize, g_nMaxLogSize = $g_nMaxLogSize\n";
    return 0;
}

#@brief 打开日志文件 日志的文件名产生规则：日志头_时间_进程ID_日志计数.log
#OpenLog();
#@return  
# - -20105  打开日志文件失败
# - 0       成功
sub OpenLog
{
    
    #日志的文件名产生规则：日志头_时间_进程ID_日志计数.log
    $g_szTmpLogName = "$g_szLogPath/$g_szLogFileHead"."_".$g_szTmpLogDay."_".$g_nCurrCycleFileNum.".log.tmp";	
    open($g_LOGFD, ">>$g_szTmpLogName") 
        or print STDERR "E.[LOGFUN]OpenLog: Can't open log file [$g_szTmpLogName]\n" and return LG_OPEN_LOG_ERROR;
    
    #打开autoflush模式
    $g_LOGFD->autoflush(1);    
    return 0;
}



#@brief 关闭日志文件
#CloseLog();
#@return  
# - 20107   无效的日志描述符
# - 0       成功
sub CloseLog
{
    if(!defined($g_LOGFD))
    {
        return LG_INVALID_LOG_FD_ERROR;   
    }
    close($g_LOGFD); 
    if($g_szTmpLogName =~ /^(.*)\.tmp$/)
    {
        my $szLogTarget = $1;
        #将tmp文件移动为目标文件
        rename($g_szTmpLogName, $szLogTarget);
    }
    return 0;
}

#@brief 切换文件
#@node 非公共函数
sub SwitchLogFile
{
    my $szNowDay = substr(LogTime2Str(time()), 0, 8);
    #关闭日志文件
    CloseLog();
    
    if($szNowDay ne $g_szTmpLogDay)#日期切换
    {
        print STDOUT "I.[LOGFUN]SwitchLogFile: SwitchLogFile g_szTmpLogDay[$g_szTmpLogDay], szNowDay[$szNowDay]\n";
        $g_nCurrCycleFileNum = 0; 
        $g_nTmpLogSize = 0;
        $g_szTmpLogDay = $szNowDay;
        
    }
    else  # 超过日志大小切换
    {
        $g_nCurrCycleFileNum++;
        $g_nTmpLogSize = 0;
    }   
    
    #打开日志文件  
    if(OpenLog() < 0)
    {
        return LG_OPEN_LOG_ERROR;   
    }
    
    return 0; 
}

#检查日志文件级别
sub CheckLevelFile
{
    my $level = shift;
    
    return $g_logLevelGroup{$level} >= $g_logLevelGroup{$g_fileLogLevel};
}
#检查终端输出级别
sub CheckLevelTerm
{
    my $level = shift;
    
    return $g_logLevelGroup{$level} >= $g_logLevelGroup{$g_termLogLevel};       
}

#@brief 写日志
#WriteLog(level, @argv);
#@param   [in]   $level    日志级别      Required
#@param   [in]   @argv     日志内容,类似printf()中的内容参数   Required
#@return  
# - -20106  无效日志级别
# - -20107  无效日志文件描述符
# - -20108  切换日志失败
# - 0       成功
sub WriteLog
{
    # 日志级别   可变参数
    my ($level, @argv) = @_;   
    
    if(!defined $g_LOGFD)
    {
        #OpenLog(); 
        print STDERR "E.[LOGFUN]WriteLog:  Undefined g_LOGFD\n";
        return LG_INVALID_LOG_FD_ERROR;  
    }
    if(!exists($g_logLevelGroup{$level}))
    {
        print STDERR "E.[LOGFUN]WriteLog:  Unknow log level[$level]\n";
        return LG_INVALID_LOG_LEVEL_ERROR;   
    }
    
    my $nowDate = LogTime2Str(time());
    my ($lSec, $lUsec) = gettimeofday;    #精确到微秒
    $lUsec = sprintf("%06d", $lUsec);
    
    my $nIsPrintFile = CheckLevelFile($level);
    my $nIsPrintTerm = CheckLevelTerm($level);
    
    my $szFormat;
    my $szMsg;
    if($nIsPrintFile || $nIsPrintTerm)
    {
        #取第一个参数为格式字串
        foreach (@argv)
        {
            $_ = '' if(!defined($_));
        }
        $szFormat = shift(@argv);
        $szMsg = sprintf($szFormat, @argv);
    }
    
    if($nIsPrintFile)
    {
        print $g_LOGFD "$level.[$nowDate:$lUsec][$g_szLogContentHead] $szMsg";  
        $g_nTmpLogSize ++;
    }
    if($nIsPrintTerm)
    {
        print STDOUT "$level.[$nowDate:$lUsec][$g_szLogContentHead] $szMsg";  
    }
    
    if($g_nTmpLogSize >= $g_nMaxLogSize || substr($nowDate, 0, 8) ne $g_szTmpLogDay)
    {
        #切换日志
        if(SwitchLogFile() < 0)
        {
            print STDERR "E.[LOGFUN]WriteLog:  SwitchLogFile err\n";
            return LG_SWITCH_LOG_ERROR;   
        }
    }
    return 0;
}

#扩展的写日志函数
sub WriteTrace  (@)  {return WriteLog('T', @_);}
sub WriteDebug  (@)  {return WriteLog('D', @_);}
sub WriteInfo   (@)  {return WriteLog('I', @_);}
sub WriteWarn   (@)  {return WriteLog('W', @_);}
sub WriteError  (@)  {return WriteLog('E', @_);}
sub WriteFatal  (@)  {return WriteLog('F', @_);}
sub WriteClosure(@)  {return WriteLog('C', @_);}



#把时间戳转换成字符串格式:YYYYMMDDHHMMSS.
#若没有输入参数,则默认返回当前日期
#若输入1970 以来的秒数,返回此秒数的日期
sub LogTime2Str
{
   my($timestamp)=@_;

   if(!defined($timestamp))
   {
      $timestamp = scalar(time());
   }
   
   my($sec ,$min ,$hour ,$day ,$mon ,$year ,undef ,undef ,undef) = localtime($timestamp);
   return sprintf("%04d%02d%02d%02d%02d%02d",$year + 1900 ,$mon +1 ,$day ,$hour,$min,$sec);
}

#控制结果文件统一打印
#专用于oamTariffAuto_oam.pl
sub printRetLog(){
	my ($level,$info,$pRef) = @_;
	
	if((!defined $level) or (!defined $info)){
		 
		  WriteError("日志级别或业务类型为空，请检查。\n");
		  exit(-1);
	} 
	if($level eq "I"){
		
		print $$pRef ("[I].$info\n");
		
	}elsif($level eq "R"){
		
		print $$pRef ("[R].$info\n");
		
	}elsif($level eq "Begin"){
		
		print $$pRef ("[B].business:$info begin\n");
		
	}elsif($level eq "End"){
		
		print $$pRef ("[N].business:$info end\n");
		
	}
	
}

1;
