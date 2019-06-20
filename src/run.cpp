#pragma comment (lib,"Urlmon.lib")
#pragma comment( linker, "/subsystem:\"windows\" /entry:\"mainCRTStartup\"" )

#include <Windows.h>

int main(){
	WinExec("javaw -jar Encrypt.jar",SW_SHOW);
}