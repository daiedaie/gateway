#!/usr/bin/perl -w
#
#@defgroup   ��־ģ��
#@author     gary
#@version    1.0
#@date       20101027

#����������
#
#����
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
use Time::HiRes qw(gettimeofday);                             #��ȷ��΢��
use IO::Handle;    #autoflush

####������####
use constant LG_SET_LOG_LEVEL_ERROR                     =>  -20101;     #������־����ʧ��
use constant LG_INVALID_PATH_ERROR                      =>  -20102;     #����ЧĿ¼
use constant LG_INVALID_LOG_HEAD_ERROR                  =>  -20103;     #����Ч��־ͷ
use constant LG_INVALID_LOG_SIZE_ERROR                  =>  -20104;     #����Ч��־��С
use constant LG_OPEN_LOG_ERROR                          =>  -20105;     #����־ʧ��
use constant LG_INVALID_LOG_LEVEL_ERROR                 =>  -20106;     #����Ч��־����
use constant LG_INVALID_LOG_FD_ERROR                    =>  -20107;     #����Ч��־������
use constant LG_SWITCH_LOG_ERROR                        =>  -20108;     #�л���־�ļ�ʧ��

####ȫ�ֱ���####
our $g_szLogPath         = './';                              #��־·��
our %g_logLevelGroup     = ('T'  => 0,     #TRACE
                            'D'  => 20,    #DEBUG
                            'I'  => 40,    #INFO
                            'W'  => 60,    #WARN
                            'E'  => 80,    #ERROR
                            'F'  => 100,   #FATAL
                            'C'  => 120);  #CLOSURE/END

our $g_fileLogLevel      = 'D';                               #��־�ļ�����
our $g_termLogLevel      = 'D';                               #�ն��������
our $g_szLogFileHead     = 'logV1';                           #��־�ļ�ͷ
our $g_szLogContentHead  = 'logV1';                           #��־����ͷ
our $g_nMaxLogSize       = 100000;                            #��־�������

our $g_nCurrCycleFileNum = 0;                                 #��ǰ��־�ļ���
our $g_szTmpLogName;                                          #��־�ļ�ȫ·����
our $g_szTmpLogDay       = substr(LogTime2Str(time()), 0, 8); #��ǰ����
our $g_nTmpLogSize       = 0;                                 #��ǰ��־��С

our $g_LOGFD;                                                 #ȫ����־�ļ�������



#@brief ������־����
#SetLogLevel(logLevel, termLevel);
#@param [in]  $logLevel    �ļ���־����     Required 
#@param [out] $termLevel   ��Ļ��־����     Required
#@return  
# - -20101  ������־����ʧ��
# - 0       ���óɹ�  
sub SetLogLevel
{
    #�ִ�����ֵ�� 
    #�ļ�����   ��Ļ���� 
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
#������־�����Ӻ���, 
#@node ���ǹ�������    
sub SetLevelSub
{
    #�ִ�����ֵ��   ����ָ�� 
    my ($logLevel, $pLevelVar) = @_;
    
    if(exists($g_logLevelGroup{$logLevel}))
    {
        #�ַ���
        $$pLevelVar = $logLevel;    
    }       
    else
    {
        #��ֵ��
        if($logLevel =~ /^\d+$/)
        {
            my $tmpLevel = 0;
            #ȡ��ӽ�����־�����Сֵ
            foreach my $value (sort{$a <=> $b} values %g_logLevelGroup)
            {
                last if($logLevel < $value);
                $tmpLevel = $value;   
            }    
            #ת���ַ�
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

#@brief ������־·��
#SetLogPath(szLogPath);
#@param [in]  $szLogPath    ��־·��     Required 
#@return  
# - -20102  ����Ч��־·��
# - 0       ���óɹ�  
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

#@brief ������־ͷ
#SetLogHead(szFileHead, szContentHead);
#@param [in]  $szFileHead     ��־�ļ�ͷ     Required 
#@param [in]  $szContentHead  ��־����ͷ     Required 
#@return  
# - -20103  ����Ч��־·��
# - 0       ���óɹ�  
sub SetLogHead
{
    #�ļ�ͷ       ��־����ͷ
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

#@brief ������־��С
#SetLogSize(size);
#@param [in]  $size     ��־��С0<int($size)<10000000    Required 
#@return  
# - -20104  ����Ч��־��С
# - 0       ���óɹ�  
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

#@brief ����־�ļ� ��־���ļ�������������־ͷ_ʱ��_����ID_��־����.log
#OpenLog();
#@return  
# - -20105  ����־�ļ�ʧ��
# - 0       �ɹ�
sub OpenLog
{
    
    #��־���ļ�������������־ͷ_ʱ��_����ID_��־����.log
    $g_szTmpLogName = "$g_szLogPath/$g_szLogFileHead"."_".$g_szTmpLogDay."_".$g_nCurrCycleFileNum.".log.tmp";	
    open($g_LOGFD, ">>$g_szTmpLogName") 
        or print STDERR "E.[LOGFUN]OpenLog: Can't open log file [$g_szTmpLogName]\n" and return LG_OPEN_LOG_ERROR;
    
    #��autoflushģʽ
    $g_LOGFD->autoflush(1);    
    return 0;
}



#@brief �ر���־�ļ�
#CloseLog();
#@return  
# - 20107   ��Ч����־������
# - 0       �ɹ�
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
        #��tmp�ļ��ƶ�ΪĿ���ļ�
        rename($g_szTmpLogName, $szLogTarget);
    }
    return 0;
}

#@brief �л��ļ�
#@node �ǹ�������
sub SwitchLogFile
{
    my $szNowDay = substr(LogTime2Str(time()), 0, 8);
    #�ر���־�ļ�
    CloseLog();
    
    if($szNowDay ne $g_szTmpLogDay)#�����л�
    {
        print STDOUT "I.[LOGFUN]SwitchLogFile: SwitchLogFile g_szTmpLogDay[$g_szTmpLogDay], szNowDay[$szNowDay]\n";
        $g_nCurrCycleFileNum = 0; 
        $g_nTmpLogSize = 0;
        $g_szTmpLogDay = $szNowDay;
        
    }
    else  # ������־��С�л�
    {
        $g_nCurrCycleFileNum++;
        $g_nTmpLogSize = 0;
    }   
    
    #����־�ļ�  
    if(OpenLog() < 0)
    {
        return LG_OPEN_LOG_ERROR;   
    }
    
    return 0; 
}

#�����־�ļ�����
sub CheckLevelFile
{
    my $level = shift;
    
    return $g_logLevelGroup{$level} >= $g_logLevelGroup{$g_fileLogLevel};
}
#����ն��������
sub CheckLevelTerm
{
    my $level = shift;
    
    return $g_logLevelGroup{$level} >= $g_logLevelGroup{$g_termLogLevel};       
}

#@brief д��־
#WriteLog(level, @argv);
#@param   [in]   $level    ��־����      Required
#@param   [in]   @argv     ��־����,����printf()�е����ݲ���   Required
#@return  
# - -20106  ��Ч��־����
# - -20107  ��Ч��־�ļ�������
# - -20108  �л���־ʧ��
# - 0       �ɹ�
sub WriteLog
{
    # ��־����   �ɱ����
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
    my ($lSec, $lUsec) = gettimeofday;    #��ȷ��΢��
    $lUsec = sprintf("%06d", $lUsec);
    
    my $nIsPrintFile = CheckLevelFile($level);
    my $nIsPrintTerm = CheckLevelTerm($level);
    
    my $szFormat;
    my $szMsg;
    if($nIsPrintFile || $nIsPrintTerm)
    {
        #ȡ��һ������Ϊ��ʽ�ִ�
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
        #�л���־
        if(SwitchLogFile() < 0)
        {
            print STDERR "E.[LOGFUN]WriteLog:  SwitchLogFile err\n";
            return LG_SWITCH_LOG_ERROR;   
        }
    }
    return 0;
}

#��չ��д��־����
sub WriteTrace  (@)  {return WriteLog('T', @_);}
sub WriteDebug  (@)  {return WriteLog('D', @_);}
sub WriteInfo   (@)  {return WriteLog('I', @_);}
sub WriteWarn   (@)  {return WriteLog('W', @_);}
sub WriteError  (@)  {return WriteLog('E', @_);}
sub WriteFatal  (@)  {return WriteLog('F', @_);}
sub WriteClosure(@)  {return WriteLog('C', @_);}



#��ʱ���ת�����ַ�����ʽ:YYYYMMDDHHMMSS.
#��û���������,��Ĭ�Ϸ��ص�ǰ����
#������1970 ����������,���ش�����������
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

#���ƽ���ļ�ͳһ��ӡ
#ר����oamTariffAuto_oam.pl
sub printRetLog(){
	my ($level,$info,$pRef) = @_;
	
	if((!defined $level) or (!defined $info)){
		 
		  WriteError("��־�����ҵ������Ϊ�գ����顣\n");
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
