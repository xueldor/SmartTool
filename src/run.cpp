#pragma comment (lib,"Urlmon.lib")
#pragma comment( linker, "/subsystem:\"windows\" /entry:\"mainCRTStartup\"" )

#include <Windows.h>
#include <iostream>
#include <fstream>
#include <string>
#include <io.h>
using namespace std;

void listFiles(const char * dir);
string getSingleJarFileName();
string readTxtFirstLine(string file);
void appendToFile(string file,string txt);
string trimLeft(string str);

int main(){
	//listFiles(".\\*");

	string fname = "run.ini";
	string logfile = "run.log";
	string txt = readTxtFirstLine(fname);
	if(txt.length() != 0){
		txt = trimLeft(txt);//去掉文件开头的不可视字符和英文空格,有些文件使用utf-8-bom
		string command = "javaw -jar \"" + txt + "\"";
		WinExec(command.c_str(),SW_SHOW);
	}else{
		try{
			string jar = getSingleJarFileName();
			string command = "javaw -jar \"" + jar + "\"";
			WinExec(command.c_str(),SW_SHOW);
		}catch(int e){
			if (e == 1){
				cout<<"没有找到jar类型文件"<<endl;
			}else if(e == 2){
				appendToFile(logfile,"找到超过1个jar文件,请在run.ini中指定具体的文件名");
			}else{
				cout<<"!!!!!"<<endl;
			}

		};
	}
}
string readTxtFirstLine(string file){
	ifstream infile; 
	infile.open(file.data());

	string txt;
	getline(infile,txt);
	infile.close();

	return txt;
}
void appendToFile(string file,string txt){
	ofstream f1(file.data(),ios::app);
	if(!f1)return;//打开文件失败则结束运行
	f1<<txt<<endl;
    f1.close();
}
string getSingleJarFileName(){
	const char* dir = ".\\*.jar";//当前目录包含.jar的文件
	string jarnames;

	intptr_t handle;
	_finddata_t findData;

	handle = _findfirst(dir,&findData);
	if(handle == -1){
		throw 1;
	}
	int index = 0;
	do
	{
		if(findData.attrib & _A_ARCH){
			string curstr = findData.name;
			if(curstr.substr(curstr.length() - 4,curstr.length()).compare(".jar") == 0){
				if(index == 0){
					jarnames = curstr;
				}
				++index;
			}
		}
	} while (_findnext(handle,&findData) == 0);
	_findclose(handle);
	if (index == 0){
		throw 1;
	}else if(index > 1){
		throw 2;
	}else{
		return jarnames;
	}
}

void listFiles(const char * dir){
	intptr_t handle;
	_finddata_t findData;

	handle = _findfirst(dir,&findData);
	if(handle == -1){
		cout<<"fail to find first file"<<endl;
		return;
	}
	do
	{
		//跳过.和..目录
		if(strcmp(findData.name,".") != 0 && strcmp(findData.name,"..") != 0){
			if(findData.attrib & _A_SUBDIR){
				cout<<"find dir  "<<findData.name <<endl;
			}else{
				cout<<"find file  "<<findData.name << "\t,file length " << findData.size<<" bytes" <<endl;
			}
		}
	} while (_findnext(handle,&findData) == 0);
	_findclose(handle);
}

string trimLeft(string str){
	int start = 0;
	while(str.at(start) < 0x21 || str.at(start) > 0x7E){
		start++;
	}
	str = str.substr(start);
	return str;
}