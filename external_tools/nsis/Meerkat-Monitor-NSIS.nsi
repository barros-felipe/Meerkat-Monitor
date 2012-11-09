; Meerkat Monitor NSIS

!include "MUI2.nsh"
!define MUI_ABORTWARNING
!define MUI_ICON "../../src/resources/installer.ico"
!define MUI_UNICON "../../src/resources/installer.ico"
!define MUI_SPECIALBITMAP "../../src/resources/meerkat-small.png"

;--------------------------------
; Settings
Name "Meerkat-Monitor"
OutFile "Meerkat-Monitor-Installer.exe"
InstallDir $PROGRAMFILES\Meerkat-Monitor
RequestExecutionLevel user
;--------------------------------

;--------------------------------
; Pages 
!insertmacro MUI_PAGE_LICENSE "../../COPYRIGHT"
!insertmacro MUI_PAGE_COMPONENTS
!insertmacro MUI_PAGE_DIRECTORY
!insertmacro MUI_PAGE_INSTFILES
!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

;--------------------------------
; Languages 
!insertmacro MUI_LANGUAGE "English"
 
;--------------------------------
; The stuff to install
Section "Application Files (required)"

  SectionIn RO
  
  ; Set output path to the installation directory.
  SetOutPath $INSTDIR
  
  ; Put file there
  File /r "..\..\bundle\*"
  
  ; Write the installation path into the registry
  WriteRegStr HKLM SOFTWARE\Meerkat-Monitor "Install_Dir" "$INSTDIR"
  
  ; Write the uninstall keys for Windows
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Meerkat-Monitor" "DisplayName" "Meerkat-Monitor-daemon.exe"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Meerkat-Monitor" "Dashboard URL" '"$INSTDIR\Meerkat-Monitor-Dashboard.URL"'
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Meerkat-Monitor" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteUninstaller "uninstall.exe"
  
SectionEnd


; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\Meerkat-Monitor"
  CreateShortCut "$SMPROGRAMS\Meerkat-Monitor\Meerkat-Monitor Daemon.lnk" "$INSTDIR\Meerkat-Monitor-daemon.exe" "" "$INSTDIR\Meerkat-Monitor-daemon.exe" 0
  CreateShortCut "$SMPROGRAMS\Meerkat-Monitor\Meerkat-Monitor Dashboard.lnk" "$INSTDIR\Meerkat-Monitor-Dashboard.URL" "" "C:\WINNT\system32\url.dll" 0
  CreateShortCut "$SMPROGRAMS\Meerkat-Monitor\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  
SectionEnd

;--------------------------------
; Uninstaller
Section "Uninstall"
  
  ; Remove registry keys
  DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Meerkat-Monitor"
  DeleteRegKey HKLM SOFTWARE\Meerkat-Monitor

  ; Remove files and uninstaller
  RMDir /r $INSTDIR
  
  ; Remove shortcuts, if any
  Delete "$SMPROGRAMS\Meerkat-Monitor\*.*"

  ; Remove directories used
  RMDir "$SMPROGRAMS\Meerkat-Monitor"
  RMDir "$INSTDIR"

SectionEnd
