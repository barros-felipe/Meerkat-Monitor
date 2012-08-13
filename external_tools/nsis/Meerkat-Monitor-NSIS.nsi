; Meerkat Monitor NSIS

;--------------------------------
Name "Meerkat-Monitor"
OutFile "Meerkat-Monitor-Installer.exe"
InstallDir $PROGRAMFILES\Meerkat-Monitor
;--------------------------------

; Pages
Page components
Page directory
Page instfiles

UninstPage uninstConfirm
UninstPage instfiles

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
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Meerkat-Monitor" "DisplayName" "Meerkat-Monitor"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\Meerkat-Monitor" "UninstallString" '"$INSTDIR\uninstall.exe"'
  WriteUninstaller "uninstall.exe"
  
SectionEnd


; Optional section (can be disabled by the user)
Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\Meerkat-Monitor"
  CreateShortCut "$SMPROGRAMS\Meerkat-Monitor\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\Meerkat-Monitor\Meerkat-Monitor.lnk" "$INSTDIR\Meerkat-Monitor.exe" "" "$INSTDIR\Meerkat-Monitor.exe" 0
  
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
