; -- xins.iss --
;
; $Id: xins.iss,v 1.25 2007/09/17 13:35:40 agoubard Exp $

[Setup]
AppName=XINS
AppVerName=XINS version %%VERSION%%
DefaultDirName={pf}\xins
VersionInfoVersion=%%VERSION%%
OutputDir=%%PROJECT_HOME%%\xins\build
OutputBaseFilename=xins-%%VERSION%%
SetupIconFile=..\..\..\xins.ico
WizardImageFile=bigxinslogo.bmp
WizardSmallImageFile=smallxinslogo.bmp
UninstallDisplayIcon={app}\xins.ico
DefaultGroupName=XINS
DisableProgramGroupPage=yes
LicenseFile=%%PROJECT_HOME%%\xins-%%VERSION%%\COPYRIGHT
InfoBeforeFile=xins-info1.txt

[Files]
Source: "%%PROJECT_HOME%%\xins-%%VERSION%%\*"; DestDir: "{app}"; Flags: recursesubdirs
Source: "..\..\..\xins.ico"; DestDir: "{app}"

[Icons]
Name: "{group}\Run demo"; Filename: "{app}\demo\rundemo.bat"; WorkingDir: "{app}\demo\"; IconFilename: "{app}\xins.ico"
Name: "{group}\Readme"; Filename: "{app}\README.html"; WorkingDir: "{app}"
Name: "{group}\Guideline"; Filename: "{app}\docs\index.html"; WorkingDir: "{app}"
Name: "{group}\Primer"; Filename: "{app}\docs\primer\primer.html"; WorkingDir: "{app}"
;Name: "{group}\{cm:UninstallProgram,XINS}"; Filename: "{uninstallexe}"

[Registry]
Root: HKCU; Subkey: "Environment"; ValueType: string; ValueName: "XINS_HOME"; ValueData: "{app}"; Flags: uninsdeletevalue
Root: HKCU; Subkey: "Environment"; ValueType: expandsz; ValueName: "PATH"; ValueData: "{code:GetNewPath|{app}\bin}"; Flags: preservestringtype
;"{app}\bin;{reg:HKCU\Environment,PATH|""}"; Flags: preservestringtype

[Run]
Filename: "{app}\README.html"; Description: "View the README file."; Flags: postinstall nowait shellexec skipifsilent
Filename: "{app}\demo\rundemo.bat"; WorkingDir: "{app}\demo"; Description: "Compile and run demo."; Flags: postinstall nowait skipifsilent unchecked
;Filename: "set XINS_HOME={app};{app}\bin\xins.bat download-tools"; WorkingDir: "{app}\demo\xins-project"; Description: "Download optional tools libraries."; Flags: postinstall nowait skipifsilent unchecked

[UninstallDelete]
Type: filesandordirs; Name: "{app}\demo\xins-project\build\*"
Type: dirifempty; Name: "{app}\demo\xins-project\build"
Type: dirifempty; Name: "{app}\demo\xins-project"
Type: dirifempty; Name: "{app}\demo"
Type: dirifempty; Name: "{app}"

[Code]
var
  CurrentPath: String;
  UsersPath: String;
  
function ShouldSkipPage(PageID: Integer): Boolean;
begin
  { Do no show the InfoBeforeFile page if the environment variables are correct. }
  if (PageID = wpInfoBefore) and (not (GetEnv('ANT_HOME') = '')) and (not (GetEnv('JAVA_HOME') = '')) then begin
    Result := True;
  end else begin
    Result := False;
  end;
end;

function GetNewPath(XPath: String): String;
begin
  CurrentPath := ExpandConstant('{reg:HKCU\Environment,PATH}');
  UsersPath := ExpandConstant('{reg:HKLM\SYSTEM\CurrentControlSet\Control\Session Manager\Environment,PATH}');
  if (Length(CurrentPath) = 0) then begin
    Result := XPath;
  end else if ((Pos(XPath, CurrentPath) = 0) and (Pos(XPath, UsersPath) = 0)) then begin
    Result := XPath + ';' + CurrentPath;
  end else begin
    Result := CurrentPath;
  end;
end;

