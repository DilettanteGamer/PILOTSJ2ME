/* Automated Ghidra 11.3.2 C pseudocode. NOT original source or buildable C.
 * Input: AUTORUN.EXE
 * Addresses and inferred types require manual validation.
 */

/* Address: 00403010 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_00403010(LPCSTR param_1,LPCSTR param_2)

{
  _DAT_00405944 =
       GetPrivateProfileStringA(param_1,param_2,&DAT_00405074,&DAT_00405844,0x100,&DAT_00405744);
  return;
}



/* Address: 0040303c */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_0040303c(LPCSTR param_1,LPCSTR param_2,INT param_3)

{
  _DAT_00405948 = GetPrivateProfileIntA(param_1,param_2,param_3,&DAT_00405744);
  return;
}



/* Address: 00403064 */

void FUN_00403064(void)

{
  DdeInitializeA(&DAT_0040594c,(PFNCALLBACK)&LAB_0040305c,0x10,0);
  return;
}



/* Address: 00403078 */

void FUN_00403078(void)

{
  DdeUninitialize(DAT_0040594c);
  return;
}



/* Address: 00403084 */

bool FUN_00403084(LPBYTE param_1)

{
  HSZ hszService;
  HCONV hConv;
  int iVar1;
  HDDEDATA pHVar2;
  bool bVar3;
  HCONV hConv_00;
  HSZ hszItem;
  UINT wFmt;
  UINT wType;
  DWORD dwTimeout;
  PCONVCONTEXT pCC;
  LPDWORD pdwResult;
  
  bVar3 = false;
  pCC = (PCONVCONTEXT)0x0;
  hszService = DdeCreateStringHandleA(DAT_0040594c,s_PROGMAN_00405075,0x3ec);
  hConv = DdeConnect(DAT_0040594c,hszService,hszService,pCC);
  if (hConv != (HCONV)0x0) {
    pdwResult = (LPDWORD)0x0;
    dwTimeout = 1000;
    wType = 0x4050;
    wFmt = 1;
    hszItem = (HSZ)0x0;
    hConv_00 = hConv;
    iVar1 = lstrlenA((LPCSTR)param_1);
    pHVar2 = DdeClientTransaction(param_1,iVar1 + 1,hConv_00,hszItem,wFmt,wType,dwTimeout,pdwResult)
    ;
    bVar3 = pHVar2 != (HDDEDATA)0x0;
    DdeDisconnect(hConv);
  }
  DdeFreeStringHandle(DAT_0040594c,hszService);
  return bVar3;
}



/* Address: 004030fc */

undefined4 * FUN_004030fc(LPCSTR param_1)

{
  HSZ hszService;
  HCONV hConv;
  HSZ hszItem;
  HDDEDATA hData;
  undefined4 *puVar1;
  UINT wFmt;
  UINT wType;
  DWORD dwTimeout;
  PCONVCONTEXT pCC;
  LPDWORD pdwResult;
  uint local_8;
  LPBYTE local_4;
  
  puVar1 = (undefined4 *)0x0;
  pCC = (PCONVCONTEXT)0x0;
  hszService = DdeCreateStringHandleA(DAT_0040594c,s_PROGMAN_00405075,0x3ec);
  hConv = DdeConnect(DAT_0040594c,hszService,hszService,pCC);
  if (hConv != (HCONV)0x0) {
    pdwResult = (LPDWORD)0x0;
    dwTimeout = 1000;
    wType = 0x20b0;
    wFmt = 1;
    hszItem = DdeCreateStringHandleA(DAT_0040594c,param_1,0x3ec);
    hData = DdeClientTransaction((LPBYTE)0x0,0,hConv,hszItem,wFmt,wType,dwTimeout,pdwResult);
    DdeFreeStringHandle(DAT_0040594c,hszItem);
    if (hData != (HDDEDATA)0x0) {
      local_4 = DdeAccessData(hData,&local_8);
      if ((local_4 != (LPBYTE)0x0) && (local_8 != 0)) {
        puVar1 = (undefined4 *)FUN_00403738(local_8);
        if (puVar1 != (undefined4 *)0x0) {
          FUN_004037a0(puVar1,(undefined4 *)local_4,local_8);
        }
      }
      DdeUnaccessData(hData);
      DdeFreeDataHandle(hData);
    }
    DdeDisconnect(hConv);
  }
  DdeFreeStringHandle(DAT_0040594c,hszService);
  return puVar1;
}



/* Address: 004031e0 */

bool FUN_004031e0(void)

{
  uint *puVar1;
  uint *puVar2;
  char cVar3;
  
  cVar3 = '\x01';
  puVar1 = FUN_004030fc(s_Groups_0040507d);
  if (puVar1 != (uint *)0x0) {
    FUN_00403010(&DAT_00405084,s_group_00405089);
    puVar2 = FUN_004035e4(puVar1,(uint *)&DAT_00405844);
    cVar3 = '\x01' - (puVar2 != (uint *)0x0);
    FUN_004036ec(puVar1);
  }
  return (bool)cVar3;
}



/* Address: 00403228 */

void FUN_00403228(void)

{
  uint uVar1;
  uint uVar2;
  BYTE local_208 [256];
  CHAR aCStack_108 [8];
  BYTE aBStack_100 [256];
  
  FUN_00403010(&DAT_00405084,s_group_00405089);
  wsprintfA((LPSTR)local_208,s__CreateGroup__s___00405095,&DAT_00405844);
  FUN_00403084(local_208);
  uVar2 = 1;
  uVar1 = FUN_0040303c(&DAT_00405084,s_items_0040508f,0);
  if (uVar1 != 0) {
    do {
      wsprintfA(aCStack_108,s_item_d_004050c5,uVar2);
      FUN_00403010(aCStack_108,s_command_004050bd);
      wsprintfA((LPSTR)local_208,&DAT_00405844,&DAT_00405644);
      FUN_00403010(aCStack_108,&DAT_004050b8);
      wsprintfA((LPSTR)aBStack_100,s__AddItem__s__s___004050a7,local_208,&DAT_00405844);
      FUN_00403084(aBStack_100);
      uVar2 = uVar2 + 1;
    } while (uVar2 <= uVar1);
  }
  return;
}



/* Address: 00403310 */

void FUN_00403310(void)

{
  int iVar1;
  CHAR local_108 [8];
  CHAR aCStack_100 [256];
  
  iVar1 = FUN_0040303c(&DAT_00405084,s_autorun_004050cc,1);
  if (iVar1 != 0) {
    wsprintfA(local_108,s_item_d_004050c5,iVar1);
    FUN_00403010(local_108,s_command_004050bd);
    wsprintfA(aCStack_100,&DAT_00405844,&DAT_00405644);
    WinExec(aCStack_100,1);
  }
  return;
}



/* Address: 0040337c */

void FUN_0040337c(void)

{
  DWORD DVar1;
  char *pcVar2;
  uint uVar3;
  uint *puVar4;
  uint uVar5;
  char *pcVar6;
  uint local_310 [64];
  CHAR aCStack_210 [256];
  CHAR aCStack_110 [8];
  CHAR aCStack_108 [8];
  uint auStack_100 [64];
  
  FUN_00403010(&DAT_00405084,s_group_00405089);
  wsprintfA((LPSTR)local_310,s__DeleteGroup__s___004050d4,&DAT_00405844);
  FUN_00403084((LPBYTE)local_310);
  DVar1 = GetVersion();
  if (3 < (byte)DVar1) {
    GetWindowsDirectoryA(aCStack_210,0x100);
    pcVar6 = &DAT_00405844;
    pcVar2 = FUN_00403930(aCStack_210,s__Start_Menu_Programs__004050e6);
    FUN_00403930(pcVar2,pcVar6);
    uVar5 = 1;
    uVar3 = FUN_0040303c(&DAT_00405084,s_items_0040508f,0);
    if (uVar3 != 0) {
      do {
        wsprintfA(aCStack_110,s_item_d_004050c5,uVar5);
        FUN_00403010(aCStack_110,&DAT_004050b8);
        wsprintfA((LPSTR)local_310,s__s__s_0040512e,aCStack_210,&DAT_00405844);
        DeleteFileA((LPCSTR)local_310);
        uVar5 = uVar5 + 1;
      } while (uVar5 <= uVar3);
    }
    RemoveDirectoryA(aCStack_210);
  }
  FUN_00403010(&DAT_00405084,s_group_00405089);
  wsprintfA((LPSTR)local_310,s__Reload__s___00405110,&DAT_00405844);
  FUN_00403084((LPBYTE)local_310);
  FUN_00403084((LPBYTE)s__Reload____00405105);
  GetTempPathA(0x100,(LPSTR)local_310);
  uVar5 = 1;
  uVar3 = FUN_0040303c(&DAT_00405084,s_tmpfiles_004050fc,1);
  if (uVar3 != 0) {
    do {
      wsprintfA(aCStack_108,s_file_d_00405127,uVar5);
      FUN_00403010(s_uninstall_0040511d,aCStack_108);
      pcVar2 = &DAT_00405844;
      puVar4 = FUN_00403a74(auStack_100,local_310);
      FUN_00403930((char *)puVar4,pcVar2);
      DeleteFileA((LPCSTR)auStack_100);
      uVar5 = uVar5 + 1;
    } while (uVar5 <= uVar3);
  }
  return;
}



/* Address: 0040354c */

undefined4 _WinMain_16(HMODULE param_1,undefined4 param_2,char *param_3)

{
  bool bVar1;
  uint *puVar2;
  char *pcVar3;
  byte *pbVar4;
  uint uVar5;
  undefined3 extraout_var;
  char cVar6;
  uint *puVar7;
  
                    /* 0x354c  1  _WinMain@16 */
  GetModuleFileNameA(param_1,(LPSTR)&DAT_00405644,0x100);
  puVar7 = (uint *)&DAT_0040513e;
  cVar6 = '.';
  puVar2 = FUN_00403a74((uint *)&DAT_00405744,&DAT_00405644);
  puVar2 = (uint *)FUN_00403bb4(puVar2,cVar6);
  FUN_00403a74(puVar2,puVar7);
  pcVar3 = FUN_00403bb4(&DAT_00405644,'\\');
  *pcVar3 = '\0';
  FUN_00403064();
  pcVar3 = s_UNINSTALL_00405134;
  pbVar4 = (byte *)FUN_00403b8c(param_3);
  uVar5 = FUN_00403a24(pbVar4,(byte *)pcVar3);
  if (uVar5 == 0) {
    FUN_0040337c();
  }
  else {
    bVar1 = FUN_004031e0();
    if (CONCAT31(extraout_var,bVar1) != 0) {
      FUN_00403228();
    }
    FUN_00403310();
  }
  FUN_00403078();
  return 0;
}



/* Address: 004035e4 */

uint * __cdecl FUN_004035e4(uint *param_1,uint *param_2)

{
  char *pcVar1;
  char *pcVar2;
  uint *puVar3;
  uint uVar4;
  uint local_408 [256];
  uint *local_8;
  uint *local_4;
  
  pcVar1 = FUN_00403ad8(param_2);
  pcVar2 = FUN_00403ad8(param_1);
  if (pcVar1 == (char *)0x0) {
    return param_1;
  }
  if (pcVar1 <= pcVar2) {
    FUN_00403bf0(local_408,pcVar1,0x100);
    puVar3 = param_2;
    do {
      local_408[(byte)*puVar3] = (uint)(pcVar1 + (-1 - ((int)puVar3 - (int)param_2)));
      puVar3 = (uint *)((int)puVar3 + 1);
    } while (*(byte *)puVar3 != 0);
    local_4 = (uint *)((char *)((int)param_2 + (int)pcVar1) + -1);
    puVar3 = (uint *)((char *)((int)param_1 + (int)pcVar1) + -1);
    local_8 = local_4;
    while( true ) {
      for (; (byte)*puVar3 == (byte)*local_4; puVar3 = (uint *)((int)puVar3 + -1)) {
        if (local_4 == param_2) {
          return puVar3;
        }
        local_4 = (uint *)((int)local_4 + -1);
      }
      uVar4 = (int)pcVar1 - ((int)local_4 - (int)param_2);
      if (uVar4 < local_408[(byte)*puVar3]) {
        uVar4 = local_408[(byte)*puVar3];
      }
      if (pcVar2 <= (char *)((int)puVar3 + (uVar4 - (int)param_1))) break;
      puVar3 = (uint *)((int)puVar3 + uVar4);
      local_4 = local_8;
    }
  }
  return (uint *)0x0;
}



/* Address: 004036ec */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void __cdecl FUN_004036ec(undefined4 *param_1)

{
  undefined4 uVar1;
  undefined4 *puVar2;
  
  LOCK();
  _DAT_00405a62 = _DAT_00405a62 + 1;
  UNLOCK();
  if (_DAT_00405a62 != 0) {
    FUN_00403c68(1);
  }
  puVar2 = param_1 + -1;
  if ((undefined4 *)0x3 < param_1 && puVar2 != (undefined4 *)0x0) {
    uVar1 = *(undefined4 *)*puVar2;
    *(undefined4 *)*puVar2 = puVar2;
    *param_1 = uVar1;
  }
  LOCK();
  _DAT_00405a62 = _DAT_00405a62 + -1;
  UNLOCK();
  if (-1 < _DAT_00405a62) {
    FUN_00403ce4(1);
  }
  return;
}



/* Address: 00403738 */

void __cdecl FUN_00403738(uint param_1)

{
  FUN_00403d60(param_1);
  return;
}



/* Address: 004037a0 */

undefined4 * __cdecl FUN_004037a0(undefined4 *param_1,undefined4 *param_2,uint param_3)

{
  undefined1 *puVar1;
  undefined1 uVar2;
  undefined4 uVar3;
  undefined4 *puVar4;
  uint uVar5;
  undefined4 *puVar6;
  undefined4 *puVar7;
  
  if ((param_3 == 0) || (param_2 == param_1)) {
    return param_1;
  }
  if ((param_2 <= param_1) && (param_1 < (undefined4 *)(param_3 + (int)param_2))) {
    puVar6 = (undefined4 *)((int)param_2 + (param_3 - 4));
    puVar7 = (undefined4 *)((int)param_1 + (param_3 - 4));
    if ((3 < param_3) && (puVar4 = puVar7, ((uint)puVar6 & 3) != 0)) {
      while (((uint)puVar4 & 3) != 0) {
        *(undefined1 *)((int)puVar7 + 3) = *(undefined1 *)((int)puVar6 + 3);
        puVar7 = (undefined4 *)((int)puVar7 + -1);
        param_3 = param_3 - 1;
        puVar6 = (undefined4 *)((int)puVar6 - 1);
        puVar4 = puVar6;
      }
    }
    uVar5 = param_3 >> 2;
    switch(uVar5) {
    case 0:
      break;
    case 5:
      uVar3 = *puVar6;
      puVar6 = puVar6 + -1;
      *puVar7 = uVar3;
      puVar7 = puVar7 + -1;
    case 4:
      uVar3 = *puVar6;
      puVar6 = puVar6 + -1;
      *puVar7 = uVar3;
      puVar7 = puVar7 + -1;
    case 3:
      uVar3 = *puVar6;
      puVar6 = puVar6 + -1;
      *puVar7 = uVar3;
      puVar7 = puVar7 + -1;
    case 2:
      uVar3 = *puVar6;
      puVar6 = puVar6 + -1;
      *puVar7 = uVar3;
      puVar7 = puVar7 + -1;
    case 1:
      uVar3 = *puVar6;
      puVar6 = puVar6 + -1;
      *puVar7 = uVar3;
      puVar7 = puVar7 + -1;
      break;
    default:
      for (; uVar5 != 0; uVar5 = uVar5 - 1) {
        *puVar7 = *puVar6;
        puVar6 = puVar6 + -1;
        puVar7 = puVar7 + -1;
      }
      switch(param_3 & 3) {
      case 0:
        goto switchD_00403922_caseD_0;
      case 1:
        goto switchD_00403922_caseD_1;
      case 3:
        goto switchD_00403922_caseD_3;
      }
      goto switchD_00403922_caseD_2;
    }
    switch(param_3 & 3) {
    case 0:
switchD_00403922_caseD_0:
      return param_1;
    case 3:
switchD_00403922_caseD_3:
      puVar1 = (undefined1 *)((int)puVar6 + 3);
      puVar6 = (undefined4 *)((int)puVar6 - 1);
      *(undefined1 *)((int)puVar7 + 3) = *puVar1;
      puVar7 = (undefined4 *)((int)puVar7 + -1);
    case 2:
switchD_00403922_caseD_2:
      puVar1 = (undefined1 *)((int)puVar6 + 3);
      puVar6 = (undefined4 *)((int)puVar6 - 1);
      *(undefined1 *)((int)puVar7 + 3) = *puVar1;
      puVar7 = (undefined4 *)((int)puVar7 + -1);
    case 1:
switchD_00403922_caseD_1:
      *(undefined1 *)((int)puVar7 + 3) = *(undefined1 *)((int)puVar6 + 3);
      return param_1;
    }
  }
  puVar6 = param_1;
  if ((3 < param_3) && (((uint)param_2 & 3) != 0)) {
    uVar5 = (uint)param_1 & 3;
    while (uVar5 != 0) {
      uVar2 = *(undefined1 *)param_2;
      param_2 = (undefined4 *)((int)param_2 + 1);
      *(undefined1 *)puVar6 = uVar2;
      puVar6 = (undefined4 *)((int)puVar6 + 1);
      param_3 = param_3 - 1;
      uVar5 = (uint)param_2 & 3;
    }
  }
  uVar5 = param_3 >> 2;
  switch(uVar5) {
  case 0:
    break;
  case 5:
    uVar3 = *param_2;
    param_2 = param_2 + 1;
    *puVar6 = uVar3;
    puVar6 = puVar6 + 1;
  case 4:
    uVar3 = *param_2;
    param_2 = param_2 + 1;
    *puVar6 = uVar3;
    puVar6 = puVar6 + 1;
  case 3:
    uVar3 = *param_2;
    param_2 = param_2 + 1;
    *puVar6 = uVar3;
    puVar6 = puVar6 + 1;
  case 2:
    uVar3 = *param_2;
    param_2 = param_2 + 1;
    *puVar6 = uVar3;
    puVar6 = puVar6 + 1;
  case 1:
    uVar3 = *param_2;
    param_2 = param_2 + 1;
    *puVar6 = uVar3;
    puVar6 = puVar6 + 1;
    break;
  default:
    for (; uVar5 != 0; uVar5 = uVar5 - 1) {
      *puVar6 = *param_2;
      param_2 = param_2 + 1;
      puVar6 = puVar6 + 1;
    }
    switch(param_3 & 3) {
    case 0:
      goto switchD_0040386e_caseD_0;
    case 1:
      goto switchD_0040386e_caseD_1;
    case 3:
      goto switchD_0040386e_caseD_3;
    }
    goto switchD_0040386e_caseD_2;
  }
  switch(param_3 & 3) {
  case 0:
switchD_0040386e_caseD_0:
    return param_1;
  case 3:
switchD_0040386e_caseD_3:
    uVar2 = *(undefined1 *)param_2;
    param_2 = (undefined4 *)((int)param_2 + 1);
    *(undefined1 *)puVar6 = uVar2;
    puVar6 = (undefined4 *)((int)puVar6 + 1);
  case 2:
switchD_0040386e_caseD_2:
    uVar2 = *(undefined1 *)param_2;
    param_2 = (undefined4 *)((int)param_2 + 1);
    *(undefined1 *)puVar6 = uVar2;
    puVar6 = (undefined4 *)((int)puVar6 + 1);
  case 1:
switchD_0040386e_caseD_1:
    *(undefined1 *)puVar6 = *(undefined1 *)param_2;
    return param_1;
  }
}



/* Address: 00403930 */

char * __cdecl FUN_00403930(char *param_1,char *param_2)

{
  char cVar1;
  uint uVar2;
  int iVar3;
  uint uVar4;
  char *pcVar5;
  char *pcVar6;
  char *pcVar7;
  
  uVar2 = 0xffffffff;
  do {
    pcVar5 = param_2;
    if (uVar2 == 0) break;
    uVar2 = uVar2 - 1;
    pcVar5 = param_2 + 1;
    cVar1 = *param_2;
    param_2 = pcVar5;
  } while (cVar1 != '\0');
  uVar2 = ~uVar2;
  iVar3 = -1;
  pcVar7 = param_1;
  do {
    pcVar6 = pcVar7;
    if (iVar3 == 0) break;
    iVar3 = iVar3 + -1;
    pcVar6 = pcVar7 + 1;
    cVar1 = *pcVar7;
    pcVar7 = pcVar6;
  } while (cVar1 != '\0');
  pcVar5 = pcVar5 + -uVar2;
  pcVar7 = pcVar6 + -1;
  for (uVar4 = uVar2 >> 2; uVar4 != 0; uVar4 = uVar4 - 1) {
    *(undefined4 *)pcVar7 = *(undefined4 *)pcVar5;
    pcVar5 = pcVar5 + 4;
    pcVar7 = pcVar7 + 4;
  }
  for (uVar2 = uVar2 & 3; uVar2 != 0; uVar2 = uVar2 - 1) {
    *pcVar7 = *pcVar5;
    pcVar5 = pcVar5 + 1;
    pcVar7 = pcVar7 + 1;
  }
  return param_1;
}



/* Address: 0040396c */

void entry(void)

{
  HMODULE pHVar1;
  UINT UVar2;
  undefined4 *unaff_FS_OFFSET;
  undefined4 uVar3;
  LPSTR pCVar4;
  undefined4 local_14;
  code *pcStack_10;
  undefined *puStack_c;
  undefined4 local_8;
  
  puStack_c = &DAT_00405148;
  pcStack_10 = FUN_0040405c;
  local_14 = *unaff_FS_OFFSET;
  *unaff_FS_OFFSET = &local_14;
  local_8 = 0;
  FUN_00404150();
  FUN_00403c0c();
  FUN_004042bc();
  DAT_00405304 = GetCommandLineA();
  FUN_00404300();
  FUN_004043e0();
  FUN_004043f0();
  FUN_00403dc0();
  uVar3 = 0;
  pCVar4 = DAT_00405304;
  pHVar1 = GetModuleHandleA((LPCSTR)0x0);
  UVar2 = _WinMain_16(pHVar1,uVar3,pCVar4);
  FUN_00403ec4(UVar2);
  *unaff_FS_OFFSET = local_14;
  return;
}



/* Address: 00403a24 */

uint __cdecl FUN_00403a24(byte *param_1,byte *param_2)

{
  byte bVar1;
  bool bVar2;
  
  while( true ) {
    bVar1 = *param_1;
    bVar2 = bVar1 < *param_2;
    if (bVar1 != *param_2) break;
    if (bVar1 == 0) {
      return 0;
    }
    bVar1 = param_1[1];
    bVar2 = bVar1 < param_2[1];
    if (bVar1 != param_2[1]) break;
    if (bVar1 == 0) {
      return 0;
    }
    bVar1 = param_1[2];
    bVar2 = bVar1 < param_2[2];
    if (bVar1 != param_2[2]) break;
    if (bVar1 == 0) {
      return 0;
    }
    bVar1 = param_1[3];
    bVar2 = bVar1 < param_2[3];
    if (bVar1 != param_2[3]) break;
    param_1 = param_1 + 4;
    param_2 = param_2 + 4;
    if (bVar1 == 0) {
      return 0;
    }
  }
  return -(uint)bVar2 | 1;
}



/* Address: 00403a74 */

uint * __cdecl FUN_00403a74(uint *param_1,uint *param_2)

{
  uint uVar1;
  char cVar2;
  char cVar3;
  uint *puVar4;
  
  puVar4 = param_1;
  while (((uint)param_2 & 3) != 0) {
    uVar1 = *param_2;
    param_2 = (uint *)((int)param_2 + 1);
    *(char *)puVar4 = (char)uVar1;
    puVar4 = (uint *)((int)puVar4 + 1);
    if ((char)uVar1 == '\0') {
      return param_1;
    }
  }
  do {
    uVar1 = *param_2;
    param_2 = param_2 + 1;
    cVar2 = (char)uVar1;
    if (cVar2 == '\0') {
      *(char *)puVar4 = '\0';
      return param_1;
    }
    cVar3 = (char)(uVar1 >> 8);
    if (cVar3 == '\0') {
      *(char *)puVar4 = cVar2;
      *(char *)((int)puVar4 + 1) = '\0';
      return param_1;
    }
    if ((uVar1 & 0xff0000) == 0) {
      *(char *)puVar4 = cVar2;
      *(char *)((int)puVar4 + 1) = cVar3;
      *(char *)((int)puVar4 + 2) = '\0';
      return param_1;
    }
    *puVar4 = uVar1;
    puVar4 = puVar4 + 1;
  } while ((uVar1 & 0xff000000) != 0);
  return param_1;
}



/* Address: 00403ad8 */

char * __cdecl FUN_00403ad8(uint *param_1)

{
  uint uVar1;
  uint *puVar2;
  uint *puVar3;
  
  puVar3 = param_1;
  while (puVar2 = puVar3, ((uint)puVar2 & 3) != 0) {
    puVar3 = (uint *)((int)puVar2 + 1);
    if ((char)*puVar2 == '\0') {
      return (char *)((int)puVar2 - (int)param_1);
    }
  }
  do {
    puVar3 = puVar2;
    uVar1 = *puVar3;
    if ((char)uVar1 == '\0') {
      return (char *)((int)puVar3 - (int)param_1);
    }
    if ((char)(uVar1 >> 8) == '\0') {
      return (char *)((int)puVar3 + (1 - (int)param_1));
    }
    if ((uVar1 & 0xff0000) == 0) {
      return (char *)((int)puVar3 + (2 - (int)param_1));
    }
    puVar2 = puVar3 + 1;
  } while ((uVar1 & 0xff000000) != 0);
  return (char *)((int)puVar3 + (3 - (int)param_1));
}



/* Address: 00403b38 */

char * __cdecl FUN_00403b38(char *param_1,char param_2)

{
  undefined4 uVar1;
  char *pcVar2;
  char cVar3;
  
  while (pcVar2 = param_1, ((uint)param_1 & 3) != 0) {
    cVar3 = *param_1;
    param_1 = param_1 + 1;
    if (cVar3 == param_2) goto LAB_00403b7b;
    if (cVar3 == '\0') {
      return (char *)0x0;
    }
  }
  while( true ) {
    uVar1 = *(undefined4 *)pcVar2;
    param_1 = pcVar2 + 4;
    if ((char)uVar1 == param_2) {
      return pcVar2;
    }
    if ((char)uVar1 == '\0') {
      return (char *)0x0;
    }
    cVar3 = (char)((uint)uVar1 >> 8);
    if (cVar3 == param_2) {
      return pcVar2 + 1;
    }
    if (cVar3 == '\0') {
      return (char *)0x0;
    }
    cVar3 = (char)((uint)uVar1 >> 0x10);
    if (cVar3 == param_2) {
      return pcVar2 + 2;
    }
    if (cVar3 == '\0') {
      return (char *)0x0;
    }
    cVar3 = (char)((uint)uVar1 >> 0x18);
    if (cVar3 == param_2) break;
    pcVar2 = param_1;
    if (cVar3 == '\0') {
      return (char *)0x0;
    }
  }
LAB_00403b7b:
  return param_1 + -1;
}



/* Address: 00403b8c */

char * __cdecl FUN_00403b8c(char *param_1)

{
  char cVar1;
  int iVar2;
  char *pcVar3;
  
  cVar1 = *param_1;
  pcVar3 = param_1;
  while (cVar1 != '\0') {
    iVar2 = FUN_004046b4((int)*pcVar3);
    *pcVar3 = (char)iVar2;
    pcVar3 = pcVar3 + 1;
    cVar1 = *pcVar3;
  }
  return param_1;
}



/* Address: 00403bb4 */

char * __cdecl FUN_00403bb4(uint *param_1,char param_2)

{
  char *pcVar1;
  char *pcVar2;
  bool bVar3;
  
  pcVar1 = FUN_00403ad8(param_1);
  pcVar2 = pcVar1 + (int)param_1;
  do {
    if (*pcVar2 == param_2) {
      return (char *)((int)param_1 + (int)pcVar1);
    }
    pcVar2 = pcVar2 + -1;
    bVar3 = pcVar1 != (char *)0x0;
    pcVar1 = pcVar1 + -1;
  } while (bVar3);
  return (char *)0x0;
}



/* Address: 00403bf0 */

undefined4 __cdecl FUN_00403bf0(undefined4 param_1,undefined4 param_2,uint param_3)

{
  FUN_004046cc(param_3);
  return param_1;
}



/* Address: 00403c0c */

void FUN_00403c0c(void)

{
  DWORD DVar1;
  char *pcVar2;
  uint uVar3;
  
  FUN_00404700(&DAT_00405a60,0xff,0x8c);
  FUN_00404700(&DAT_00405aec,0,0x118);
  FUN_00404700(&DAT_00405c04,0,0x118);
  FUN_00404700(&DAT_00405d1c,0,0x118);
  uVar3 = 0x10;
  pcVar2 = &DAT_00405e4c;
  DVar1 = GetTickCount();
  FUN_00404760(DVar1,pcVar2,uVar3);
  return;
}



/* Address: 00403c68 */

void __cdecl FUN_00403c68(uint param_1)

{
  int *piVar1;
  int iVar2;
  char *pcVar3;
  HANDLE pvVar4;
  uint local_30 [7];
  char local_14 [20];
  
  FUN_00403a74(local_30,(uint *)&DAT_00405e4c);
  pcVar3 = FUN_00404760(param_1,local_14,0x10);
  FUN_00403930((char *)local_30,pcVar3);
  piVar1 = (int *)(&DAT_00405d1c + param_1 * 4);
  iVar2 = *piVar1;
  while (iVar2 == 0) {
    pvVar4 = CreateSemaphoreA((LPSECURITY_ATTRIBUTES)0x0,0,1,(LPCSTR)local_30);
    if (pvVar4 != (HANDLE)0x0) {
      *piVar1 = (int)pvVar4;
    }
    iVar2 = *piVar1;
  }
  WaitForSingleObject(*(HANDLE *)(&DAT_00405d1c + param_1 * 4),0xffffffff);
  return;
}



/* Address: 00403ce4 */

void __cdecl FUN_00403ce4(uint param_1)

{
  int *piVar1;
  int iVar2;
  char *pcVar3;
  HANDLE pvVar4;
  uint local_30 [7];
  char local_14 [20];
  
  FUN_00403a74(local_30,(uint *)&DAT_00405e4c);
  pcVar3 = FUN_00404760(param_1,local_14,0x10);
  FUN_00403930((char *)local_30,pcVar3);
  piVar1 = (int *)(&DAT_00405d1c + param_1 * 4);
  iVar2 = *piVar1;
  while (iVar2 == 0) {
    pvVar4 = CreateSemaphoreA((LPSECURITY_ATTRIBUTES)0x0,0,1,(LPCSTR)local_30);
    if (pvVar4 != (HANDLE)0x0) {
      *piVar1 = (int)pvVar4;
    }
    iVar2 = *piVar1;
  }
  ReleaseSemaphore(*(HANDLE *)(&DAT_00405d1c + param_1 * 4),1,(LPLONG)0x0);
  return;
}



/* Address: 00403d60 */

undefined4 * __cdecl FUN_00403d60(uint param_1)

{
  undefined4 *puVar1;
  int iVar2;
  
  iVar2 = 1;
  do {
    if (param_1 == 0) {
      param_1 = 1;
    }
    puVar1 = FUN_00404800(param_1);
    if (puVar1 != (undefined4 *)0x0) {
      return puVar1;
    }
    if (DAT_00405338 == (code *)0x0) {
      return (undefined4 *)0x0;
    }
    if (DAT_00405e40 == 1) {
      (*DAT_00405338)();
      iVar2 = 1;
    }
    else if (DAT_00405e40 == 2) {
      iVar2 = (*DAT_00405338)(param_1);
    }
  } while (iVar2 != 0);
  return (undefined4 *)0x0;
}



/* Address: 00403dc0 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_00403dc0(void)

{
  short sVar1;
  DWORD DVar2;
  
  _DAT_004051c3 = GetCurrentThreadId();
  DAT_00405195 = GetEnvironmentStrings();
  DVar2 = GetVersion();
  _DAT_00405162 = (undefined2)(DVar2 >> 0x10);
  _DAT_00405166 = CONCAT11((char)DVar2,(char)(DVar2 >> 8));
  sVar1 = FUN_00404a14();
  if (sVar1 == -1) {
    FUN_004049e0(s_Not_enough_memory_0040517f);
  }
  FUN_004049a8();
  FUN_00403e0c();
  return;
}



/* Address: 00403e0c */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_00403e0c(void)

{
  undefined4 *puVar1;
  
  puVar1 = &DAT_00405644;
  while (&DAT_00405644 < puVar1) {
    puVar1 = puVar1 + -1;
    if ((code *)*puVar1 != (code *)0x0) {
      (*(code *)*puVar1)();
    }
  }
  _DAT_00405158 = 0x41;
  puVar1 = &DAT_00405644;
  while (&DAT_00405644 < puVar1) {
    puVar1 = puVar1 + -1;
    if ((code *)*puVar1 != (code *)0x0) {
      (*(code *)*puVar1)();
    }
  }
  puVar1 = (undefined4 *)&DAT_00405010;
  while (&DAT_00405000 < puVar1) {
    puVar1 = puVar1 + -1;
    if ((code *)*puVar1 != (code *)0x0) {
      (*(code *)*puVar1)();
    }
  }
  puVar1 = (undefined4 *)&DAT_00405030;
  while (&DAT_00405020 < puVar1) {
    puVar1 = puVar1 + -1;
    if ((code *)*puVar1 != (code *)0x0) {
      (*(code *)*puVar1)();
    }
  }
  return;
}



/* Address: 00403e87 */

void FUN_00403e87(void)

{
  code *pcVar1;
  undefined4 *puVar2;
  undefined4 uVar3;
  undefined4 *puVar4;
  
  uVar3 = 0;
  puVar2 = &DAT_00405644;
  while (puVar4 = puVar2, puVar4 < &DAT_00405644) {
    pcVar1 = (code *)*puVar4;
    puVar2 = puVar4 + 1;
    if (pcVar1 != (code *)0x0) {
      *puVar4 = uVar3;
      uVar3 = (*pcVar1)();
    }
  }
  FUN_004049c0();
  FreeEnvironmentStringsA(DAT_00405195);
  return;
}



/* Address: 00403ec4 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void __cdecl FUN_00403ec4(UINT param_1)

{
  int iVar1;
  
  _DAT_00405244 = 1;
  if ((DAT_00405240 == 0) && (DAT_00405154 != (int *)0x0)) {
    iVar1 = *DAT_00405154;
    while (iVar1 != 0) {
      (*(code *)*DAT_00405154)();
      DAT_00405154 = DAT_00405154 + -1;
      iVar1 = *DAT_00405154;
    }
  }
  if (DAT_00405e34 == (code *)0x0) {
    FUN_00403e87();
    if (DAT_00405240 == 0) {
      if (DAT_00405e38 != (code *)0x0) {
        (*DAT_00405e38)();
      }
      if (DAT_0040523c == 0) {
        FUN_00404050(param_1);
      }
    }
  }
  else {
    (*DAT_00405e34)(param_1);
  }
  DAT_00405240 = 0;
  DAT_0040523c = 0;
  return;
}



/* Address: 00403f5c */

LONG __cdecl FUN_00403f5c(_EXCEPTION_POINTERS *param_1)

{
  DWORD DVar1;
  code *pcVar2;
  code *pcVar3;
  LONG LVar4;
  undefined4 uVar5;
  code *pcVar6;
  int iVar7;
  
  pcVar3 = DAT_00405538;
  pcVar2 = DAT_00405534;
  pcVar6 = DAT_00405530;
  uVar5 = 0;
  iVar7 = 0;
  DVar1 = param_1->ExceptionRecord->ExceptionCode;
  if (DVar1 == 0xc000008d) {
    uVar5 = 0x82;
  }
  else if (DVar1 == 0xc000008e) {
    uVar5 = 0x83;
  }
  else if (DVar1 == 0xc000008f) {
    uVar5 = 0x86;
  }
  else if (DVar1 == 0xc0000090) {
    uVar5 = 0x81;
  }
  else if (DVar1 == 0xc0000091) {
    uVar5 = 0x84;
  }
  else if (DVar1 == 0xc0000092) {
    uVar5 = 0x8a;
  }
  else {
    if (DVar1 != 0xc0000093) {
      if (DVar1 == 0xc0000005) {
        iVar7 = 0xb;
        DAT_00405538 = (code *)0x0;
        pcVar6 = pcVar3;
      }
      else if ((DVar1 == 0xc000001d) || (DVar1 == 0xc0000096)) {
        iVar7 = 4;
        DAT_00405534 = (code *)0x0;
        pcVar6 = pcVar2;
      }
      else {
        pcVar6 = (code *)0x0;
      }
      goto LAB_00404021;
    }
    uVar5 = 0x85;
  }
  iVar7 = 8;
  DAT_00405530 = (code *)0x0;
LAB_00404021:
  if (pcVar6 != (code *)0x0) {
    if (iVar7 == 8) {
      (*pcVar6)(8,uVar5);
    }
    else {
      (*pcVar6)(iVar7);
    }
    return -1;
  }
  LVar4 = UnhandledExceptionFilter(param_1);
  return LVar4;
}



/* Address: 00404050 */

void FUN_00404050(UINT param_1)

{
                    /* WARNING: Subroutine does not return */
  ExitProcess(param_1);
}



/* Address: 0040405c */

undefined4 __cdecl FUN_0040405c(int param_1,int param_2,undefined4 param_3)

{
  int iVar1;
  undefined *puVar2;
  int iVar3;
  int iVar4;
  int *piVar5;
  int local_c;
  undefined4 local_8;
  int local_4;
  
  if ((*(byte *)(param_1 + 4) & 6) == 0) {
    local_c = param_1;
    local_8 = param_3;
    *(int **)(param_2 + -4) = &local_c;
    if (*(int *)(param_2 + 0xc) != -1) {
      local_4 = param_2 + 0x10;
      iVar4 = *(int *)(param_2 + 0xc);
      do {
        piVar5 = (int *)(iVar4 * 0xc + *(int *)(param_2 + 8));
        iVar1 = *piVar5;
        puVar2 = (undefined *)piVar5[2];
        if ((undefined *)piVar5[1] != (undefined *)0x0) {
          iVar3 = FUN_00404a24((undefined *)piVar5[1]);
          if (iVar3 == -1) {
            return 0;
          }
          if ((iVar3 != 0) && (iVar3 == 1)) {
            FUN_00404a60(param_2,(PEXCEPTION_RECORD)0x0);
            FUN_00404a88(param_2,iVar4);
            FUN_00404a24(puVar2);
            return 1;
          }
        }
        iVar4 = iVar1;
      } while (iVar1 != -1);
    }
  }
  else {
    FUN_00404a88(param_2,-1);
  }
  return 1;
}



/* Address: 00404150 */

void FUN_00404150(void)

{
  int iVar1;
  undefined4 *puVar2;
  
  if (DAT_00405248 == 0) {
    puVar2 = &DAT_00405644;
    for (iVar1 = 0x81c; iVar1 != 0; iVar1 = iVar1 + -1) {
      *(undefined1 *)puVar2 = 0;
      puVar2 = (undefined4 *)((int)puVar2 + 1);
    }
    DAT_00405248 = 1;
  }
  return;
}



/* Address: 00404184 */

int FUN_00404184(void)

{
  LPSTR lpBuffer;
  UINT uSize;
  char *pcVar1;
  int iVar2;
  int extraout_EDX;
  uint *lpBuffer_00;
  int iVar3;
  CHAR *pCVar4;
  CHAR local_88 [128];
  CHAR *pCStack_8;
  DWORD DStack_4;
  
  lpBuffer_00 = (uint *)0x0;
  pCVar4 = local_88;
  for (iVar2 = 0x80; iVar2 != 0; iVar2 = iVar2 + -1) {
    *pCVar4 = '\0';
    pCVar4 = pCVar4 + 1;
  }
  DStack_4 = GetVersion();
  if (((DStack_4 & 0x80000000) == 0) || (3 < (byte)DStack_4)) {
    iVar3 = 0x10f;
LAB_004042a0:
    if (lpBuffer_00 != (uint *)0x0) {
      FUN_00404d1c(lpBuffer_00);
    }
  }
  else {
    lpBuffer = (LPSTR)FUN_00404800(1);
    if (lpBuffer != (LPSTR)0x0) {
      uSize = GetSystemDirectoryA(lpBuffer,0);
      pcVar1 = FUN_00403ad8((uint *)s__win32s_ini_0040524c);
      lpBuffer_00 = FUN_00404b04((undefined4 *)lpBuffer,(uint)(pcVar1 + uSize + 1));
      if (lpBuffer_00 != (uint *)0x0) {
        GetSystemDirectoryA((LPSTR)lpBuffer_00,uSize);
        pcVar1 = FUN_00403ad8(lpBuffer_00);
        if ((pcVar1 + -1)[(int)lpBuffer_00] != '\\') {
          FUN_00403930((char *)lpBuffer_00,&DAT_00405258);
        }
        FUN_00403930((char *)lpBuffer_00,s_win32s_ini_0040526f);
        GetPrivateProfileStringA
                  (s_Win32s_0040525a,s_Version_00405261,s_1_0_0_00405269,local_88,0x80,
                   (LPCSTR)lpBuffer_00);
        pCStack_8 = local_88;
        iVar2 = FUN_00404b90(pCStack_8,(int)&pCStack_8,pCStack_8,&pCStack_8,10);
        pCStack_8 = pCStack_8 + 1;
        iVar3 = FUN_00404b90(&pCStack_8,extraout_EDX,pCStack_8,&pCStack_8,10);
        iVar3 = iVar3 + iVar2 * 0x100;
        goto LAB_004042a0;
      }
    }
    iVar3 = 0;
  }
  return iVar3;
}



/* Address: 004042bc */

undefined4 FUN_004042bc(void)

{
  int iVar1;
  
  iVar1 = FUN_00404184();
  if (iVar1 < 0x10f) {
    MessageBoxA((HWND)0x0,s_Wrong_Win32s_version__or_damaged_0040527b,s_Startup_Error_004052f5,0x10)
    ;
    FUN_004049e0(&DAT_0040527a);
    return 0;
  }
  return 1;
}



/* Address: 00404300 */

undefined4 * FUN_00404300(void)

{
  undefined4 *puVar1;
  DWORD DVar2;
  int iVar3;
  int *piVar4;
  
  puVar1 = FUN_00404d70(0x1004,1);
  if (puVar1 != (undefined4 *)0x0) {
    DAT_0040530c = puVar1;
    puVar1[1] = &DAT_004051ab;
    return puVar1;
  }
  FUN_004049e0(s_Thread_error_00405310);
  DVar2 = GetCurrentThreadId();
  iVar3 = 0x400;
  piVar4 = DAT_0040530c;
  while ((puVar1 = (undefined4 *)*piVar4, puVar1 == (undefined4 *)0x0 || (DVar2 != puVar1[6]))) {
    piVar4 = piVar4 + 1;
    iVar3 = iVar3 + -1;
    if (iVar3 == 0) {
      return (undefined4 *)0x0;
    }
  }
  return puVar1;
}



/* Address: 0040432e */

undefined8 __fastcall FUN_0040432e(undefined4 param_1,undefined4 param_2)

{
  DWORD DVar1;
  int iVar2;
  int iVar3;
  int *piVar4;
  
  DVar1 = GetCurrentThreadId();
  iVar3 = 0x400;
  piVar4 = DAT_0040530c;
  do {
    iVar2 = *piVar4;
    if ((iVar2 != 0) && (DVar1 == *(DWORD *)(iVar2 + 0x18))) goto LAB_00404359;
    piVar4 = piVar4 + 1;
    iVar3 = iVar3 + -1;
  } while (iVar3 != 0);
  iVar2 = 0;
LAB_00404359:
  return CONCAT44(param_2,iVar2);
}



/* Address: 004043e0 */

void FUN_004043e0(void)

{
  if (DAT_00405e3c != (code *)0x0) {
    (*DAT_00405e3c)();
  }
  return;
}



/* Address: 004043f0 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_004043f0(void)

{
  char cVar1;
  bool bVar2;
  uint *puVar3;
  uint uVar4;
  char *pcVar5;
  uint uVar6;
  uint uVar7;
  uint *puVar8;
  uint *puVar9;
  uint *puVar10;
  int local_c;
  
  DAT_00405304 = (uint *)FUN_00404658((char *)DAT_00405304);
  puVar3 = FUN_0040459c(DAT_00405304);
  if (puVar3 != (uint *)0x0) {
    local_c = 0;
    puVar9 = puVar3;
    puVar10 = puVar3;
LAB_0040443c:
    for (; cVar1 = (char)*puVar10, cVar1 != '\0'; puVar10 = (uint *)((int)puVar10 + 1)) {
      if ((cVar1 != ' ') && (cVar1 != '\t')) {
        local_c = local_c + 1;
        bVar2 = false;
        uVar7 = 0;
        puVar8 = puVar9;
        do {
          while( true ) {
            cVar1 = (char)*puVar10;
            puVar10 = (uint *)((int)puVar10 + 1);
            if (cVar1 != '\"') break;
            uVar6 = (int)uVar7 >> 0x1f;
            uVar4 = uVar7 ^ uVar6;
            for (uVar7 = (int)uVar7 / 2; 0 < (int)uVar7; uVar7 = uVar7 - 1) {
              puVar8 = (uint *)((int)puVar8 + -1);
              *(char *)puVar8 = '\0';
            }
            if (uVar6 + (uVar4 & 1) == 0) {
              bVar2 = (bool)(bVar2 ^ 1);
            }
            else {
              *(char *)((int)puVar8 + -1) = '\"';
            }
          }
          if ((cVar1 == ' ') || (cVar1 == '\t')) {
            if (!bVar2) goto LAB_004044b1;
          }
          else if (cVar1 == '\0') goto LAB_004044b1;
          if (cVar1 == '\\') {
            uVar7 = uVar7 + 1;
          }
          else {
            uVar7 = 0;
          }
          *(char *)puVar8 = cVar1;
          puVar8 = (uint *)((int)puVar8 + 1);
        } while( true );
      }
    }
    goto LAB_004044c2;
  }
LAB_0040441b:
  FUN_004049e0(s_Out_of_memory_00405324);
  return;
LAB_004044b1:
  puVar9 = (uint *)((int)puVar8 + 1);
  *(char *)puVar8 = '\0';
  if (cVar1 == '\0') goto LAB_004044c2;
  goto LAB_0040443c;
LAB_004044c2:
  DAT_00405199 = local_c;
  DAT_0040519d = FUN_00404800(local_c * 4 + 4);
  if (DAT_0040519d != (undefined4 *)0x0) {
    local_c = 0;
    if (0 < DAT_00405199) {
      local_c = 0;
      do {
        *(uint **)(local_c + (int)DAT_0040519d) = puVar3;
        pcVar5 = FUN_00403ad8(puVar3);
        puVar3 = (uint *)((int)puVar3 + (int)(pcVar5 + 1));
        local_c = local_c + 4;
      } while (local_c < DAT_00405199 * 4);
      local_c = local_c / 4;
    }
    DAT_0040519d[local_c] = 0;
    _DAT_0040515e = *DAT_0040519d;
    return;
  }
  goto LAB_0040441b;
}



/* Address: 0040459c */

uint * __cdecl FUN_0040459c(uint *param_1)

{
  char *pcVar1;
  uint *puVar2;
  DWORD DVar3;
  uint local_104 [65];
  
  pcVar1 = FUN_00403ad8(param_1);
  puVar2 = FUN_00404800((uint)(pcVar1 + 0x108));
  *(undefined1 *)puVar2 = 0;
  if (puVar2 != (uint *)0x0) {
    DVar3 = GetModuleFileNameA((HMODULE)0x0,(LPSTR)local_104,0x104);
    if (DVar3 != 0) {
      pcVar1 = FUN_00403b38((char *)local_104,'\"');
      if (pcVar1 == (char *)0x0) {
        FUN_00403930((char *)puVar2,&DAT_00405332);
        FUN_00403930((char *)puVar2,(char *)local_104);
        FUN_00403930((char *)puVar2,&DAT_00405332);
      }
      else {
        FUN_00403a74(puVar2,local_104);
      }
      FUN_00403930((char *)puVar2,&DAT_00405334);
      FUN_00403930((char *)puVar2,(char *)param_1);
      return puVar2;
    }
  }
  return (uint *)0x0;
}



/* Address: 00404658 */

char * __cdecl FUN_00404658(char *param_1)

{
  char cVar1;
  bool bVar2;
  bool bVar3;
  
  bVar2 = false;
  bVar3 = false;
  do {
    cVar1 = *param_1;
    if (cVar1 == '\"') {
      bVar2 = (bool)(bVar2 ^ 1);
      param_1 = param_1 + 1;
    }
    else if (cVar1 == '\0') {
      bVar3 = true;
    }
    else {
      if (((cVar1 == ' ') || (cVar1 == '\t')) && (!bVar2)) {
        bVar3 = true;
      }
      param_1 = param_1 + 1;
    }
  } while (!bVar3);
  cVar1 = *param_1;
  while ((cVar1 < '!' && (cVar1 != '\0'))) {
    param_1 = param_1 + 1;
    cVar1 = *param_1;
  }
  return param_1;
}



/* Address: 004046b4 */

int __cdecl FUN_004046b4(int param_1)

{
  if ((0x60 < param_1) && (param_1 < 0x7b)) {
    param_1 = param_1 + -0x20;
  }
  return param_1;
}



/* Address: 004046cc */

void __fastcall FUN_004046cc(uint param_1)

{
  undefined4 in_EAX;
  uint uVar1;
  undefined4 *unaff_EDI;
  
  for (uVar1 = param_1 >> 3; uVar1 != 0; uVar1 = uVar1 - 1) {
    *unaff_EDI = in_EAX;
    unaff_EDI[1] = in_EAX;
    unaff_EDI[2] = in_EAX;
    unaff_EDI[3] = in_EAX;
    unaff_EDI[4] = in_EAX;
    unaff_EDI[5] = in_EAX;
    unaff_EDI[6] = in_EAX;
    unaff_EDI[7] = in_EAX;
    unaff_EDI = unaff_EDI + 8;
  }
  for (uVar1 = param_1 & 7; uVar1 != 0; uVar1 = uVar1 - 1) {
    *unaff_EDI = in_EAX;
    unaff_EDI = unaff_EDI + 1;
  }
  return;
}



/* Address: 00404700 */

undefined1 * __cdecl FUN_00404700(undefined1 *param_1,undefined1 param_2,uint param_3)

{
  uint uVar1;
  uint extraout_EDX;
  undefined1 *puVar2;
  
  puVar2 = param_1;
  if (7 < param_3) {
    uVar1 = (uint)param_1 & 3;
    while (uVar1 != 0) {
      *puVar2 = param_2;
      puVar2 = puVar2 + 1;
      param_3 = param_3 - 1;
      uVar1 = (uint)puVar2 & 3;
    }
    param_2 = FUN_004046cc(param_3 >> 2);
    param_3 = extraout_EDX;
  }
  for (; param_3 != 0; param_3 = param_3 - 1) {
    *puVar2 = param_2;
    puVar2 = puVar2 + 1;
  }
  return param_1;
}



/* Address: 00404760 */

char * __cdecl FUN_00404760(uint param_1,char *param_2,uint param_3)

{
  uint uVar1;
  undefined1 *puVar2;
  char cVar3;
  int iVar5;
  char *pcVar6;
  char *pcVar7;
  char local_7 [3];
  char cVar4;
  
  iVar5 = 1;
  pcVar6 = local_7 + 2;
  local_7[2] = 0;
  if ((param_3 == 10) && ((int)param_1 < 0)) {
    iVar5 = 0;
    param_1 = -param_1;
  }
  do {
    pcVar7 = pcVar6;
    pcVar6 = pcVar7 + -1;
    uVar1 = param_1 / param_3;
    cVar4 = (char)(param_1 % param_3);
    cVar3 = cVar4 + '0';
    if ('9' < cVar3) {
      cVar3 = cVar4 + '7';
    }
    *pcVar6 = cVar3;
    param_1 = uVar1;
  } while (uVar1 != 0);
  pcVar7 = pcVar7 + -2;
  *pcVar7 = '-';
  puVar2 = &stack0xfffffffc + -(int)(pcVar7 + iVar5);
  pcVar6 = pcVar7 + iVar5;
  pcVar7 = param_2;
  for (; puVar2 != (undefined1 *)0x0; puVar2 = puVar2 + -1) {
    *pcVar7 = *pcVar6;
    pcVar6 = pcVar6 + 1;
    pcVar7 = pcVar7 + 1;
  }
  return param_2;
}



/* Address: 004047bc */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void FUN_004047bc(void)

{
  uint uVar1;
  undefined *puVar2;
  undefined4 *puVar3;
  
  puVar3 = (undefined4 *)(DAT_0040533c + _DAT_00405340);
  puVar2 = &DAT_00405344;
  for (uVar1 = 0x10000 - _DAT_00405340; uVar1 != 0; uVar1 = (int)uVar1 >> 1) {
    if ((uVar1 & 8) != 0) {
      *puVar3 = puVar2;
      FUN_00404d5c(puVar3 + 1);
      puVar3 = (undefined4 *)((int)puVar3 + *(int *)(puVar2 + 4));
    }
    puVar2 = puVar2 + 8;
  }
  return;
}



/* Address: 00404800 */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

undefined4 * __cdecl FUN_00404800(uint param_1)

{
  int *piVar1;
  int iVar2;
  int iVar3;
  undefined4 *local_c;
  
  LOCK();
  _DAT_00405a62 = _DAT_00405a62 + 1;
  UNLOCK();
  if (_DAT_00405a62 != 0) {
    FUN_00403c68(1);
  }
  param_1 = param_1 + 4;
  if (param_1 < 9) {
    param_1 = 8;
  }
  if ((param_1 & 0xffffff00) == 0) {
    iVar3 = (int)*(char *)(param_1 + 0x40542b);
  }
  else if ((param_1 & 0xffff0000) == 0) {
    iVar3 = (char)(&DAT_0040542c)[param_1 - 1 >> 8] + 8;
  }
  else if ((param_1 & 0xff000000) == 0) {
    iVar3 = (char)(&DAT_0040542c)[param_1 - 1 >> 0x10] + 0x10;
  }
  else {
    iVar3 = (char)(&DAT_0040542c)[param_1 - 1 >> 0x18] + 0x18;
  }
  piVar1 = (int *)(&DAT_00405344 + iVar3 * 8);
  iVar2 = *piVar1;
  if (iVar2 == 0) {
    if (param_1 < 0x10001) {
      if (0xffff < (uint)((&DAT_00405348)[iVar3 * 2] + _DAT_00405340)) {
        FUN_004047bc();
        DAT_0040533c = FUN_00404dac(0x10000);
        if (DAT_0040533c == (LPVOID)0xffffffff) {
          DAT_0040533c = (LPVOID)0x0;
          _DAT_00405340 = 0x10000;
          local_c = (undefined4 *)0x0;
          LOCK();
          _DAT_00405a62 = _DAT_00405a62 + -1;
          UNLOCK();
          if (_DAT_00405a62 < 0) {
            return local_c;
          }
          goto LAB_00404998;
        }
        _DAT_00405340 = 0;
      }
      local_c = (undefined4 *)((int)DAT_0040533c + _DAT_00405340);
      _DAT_00405340 = _DAT_00405340 + (&DAT_00405348)[iVar3 * 2];
      *local_c = piVar1;
    }
    else {
      local_c = (undefined4 *)FUN_00404dac((&DAT_00405348)[iVar3 * 2]);
      if (local_c == (undefined4 *)0xffffffff) {
        local_c = (undefined4 *)0x0;
        LOCK();
        _DAT_00405a62 = _DAT_00405a62 + -1;
        UNLOCK();
        if (_DAT_00405a62 < 0) {
          return local_c;
        }
        goto LAB_00404998;
      }
      *local_c = piVar1;
    }
    local_c = local_c + 1;
    LOCK();
    _DAT_00405a62 = _DAT_00405a62 + -1;
    UNLOCK();
    if (_DAT_00405a62 < 0) {
      return local_c;
    }
  }
  else {
    *piVar1 = *(int *)(iVar2 + 4);
    local_c = (undefined4 *)(iVar2 + 4);
    LOCK();
    _DAT_00405a62 = _DAT_00405a62 + -1;
    UNLOCK();
    if (_DAT_00405a62 < 0) {
      return local_c;
    }
  }
LAB_00404998:
  FUN_00403ce4(1);
  return local_c;
}



/* Address: 004049a8 */

void FUN_004049a8(void)

{
  if (DAT_00405e44 != (code *)0x0) {
    (*DAT_00405e44)(1);
  }
  return;
}



/* Address: 004049c0 */

void FUN_004049c0(void)

{
  if (DAT_00405e44 != (code *)0x0) {
    (*DAT_00405e44)(0);
  }
  return;
}



/* Address: 004049e0 */

void FUN_004049e0(char *param_1)

{
  char cVar1;
  code *pcVar2;
  HANDLE hFile;
  uint uVar3;
  DWORD nNumberOfBytesToWrite;
  char *pcVar4;
  LPDWORD lpNumberOfBytesWritten;
  LPOVERLAPPED lpOverlapped;
  
  uVar3 = 0xffffffff;
  pcVar4 = param_1;
  do {
    if (uVar3 == 0) break;
    uVar3 = uVar3 - 1;
    cVar1 = *pcVar4;
    pcVar4 = pcVar4 + 1;
  } while (cVar1 != '\0');
  nNumberOfBytesToWrite = ~uVar3 - 1;
  lpNumberOfBytesWritten = (LPDWORD)&stack0xfffffff8;
  lpOverlapped = (LPOVERLAPPED)0x0;
  hFile = GetStdHandle(0xfffffff4);
  WriteFile(hFile,param_1,nNumberOfBytesToWrite,lpNumberOfBytesWritten,lpOverlapped);
  FUN_00404050(1);
  pcVar2 = (code *)swi(3);
  (*pcVar2)();
  return;
}



/* Address: 00404a14 */

void FUN_00404a14(void)

{
  if (DAT_00405e48 != (code *)0x0) {
    (*DAT_00405e48)();
  }
  return;
}



/* Address: 00404a24 */

void __cdecl FUN_00404a24(undefined *param_1)

{
  (*(code *)param_1)(&stack0xfffffffc);
  return;
}



/* Address: 00404a60 */

void __cdecl FUN_00404a60(undefined4 param_1,PEXCEPTION_RECORD param_2)

{
  FUN_00404a74(param_2,(PVOID)0x0);
  return;
}



/* Address: 00404a74 */

void __cdecl FUN_00404a74(PEXCEPTION_RECORD param_1,PVOID param_2)

{
  int unaff_EBP;
  PVOID unaff_retaddr;
  
  RtlUnwind(*(PVOID *)(unaff_EBP + 8),unaff_retaddr,param_1,param_2);
  return;
}



/* Address: 00404a88 */

void __cdecl FUN_00404a88(int param_1,int param_2)

{
  int *piVar1;
  undefined4 *unaff_FS_OFFSET;
  undefined4 uStack_2c;
  undefined1 *puStack_28;
  undefined4 uStack_24;
  undefined4 uStack_20;
  int local_8;
  
  local_8 = *(int *)(param_1 + 0xc);
  uStack_20 = 0xffffffff;
  uStack_24 = 0;
  puStack_28 = &LAB_00404a40;
  uStack_2c = *unaff_FS_OFFSET;
  *unaff_FS_OFFSET = &uStack_2c;
  while ((local_8 != -1 && (local_8 != param_2))) {
    piVar1 = (int *)(local_8 * 0xc + *(int *)(param_1 + 8));
    local_8 = *piVar1;
    if (piVar1[1] == 0) {
      FUN_00404a24((undefined *)piVar1[2]);
    }
  }
  *unaff_FS_OFFSET = uStack_2c;
  return;
}



/* Address: 00404b04 */

undefined4 * __cdecl FUN_00404b04(undefined4 *param_1,uint param_2)

{
  undefined4 *puVar1;
  uint uVar2;
  
  if (param_1 == (undefined4 *)0x0) {
    puVar1 = FUN_00404800(param_2);
    return puVar1;
  }
  if (param_2 == 0) {
    FUN_00404d1c(param_1);
    return (undefined4 *)0x0;
  }
  uVar2 = FUN_00404e54((int)param_1);
  if (param_2 <= uVar2) {
    return param_1;
  }
  puVar1 = FUN_00404800(param_2);
  if (puVar1 == (undefined4 *)0x0) {
    return (undefined4 *)0x0;
  }
  FUN_004037a0(puVar1,param_1,uVar2);
  FUN_00404d1c(param_1);
  return puVar1;
}



/* Address: 00404b90 */

int __fastcall
FUN_00404b90(undefined4 param_1,int param_2,char *param_3,undefined4 *param_4,int param_5)

{
  byte bVar1;
  char cVar2;
  bool bVar3;
  bool bVar4;
  undefined4 *puVar5;
  char *pcVar6;
  char *pcVar7;
  int iVar8;
  int iVar9;
  
  iVar9 = 0;
  iVar8 = 0;
  bVar1 = (&DAT_00405541)[*param_3];
  pcVar6 = param_3;
  while ((bVar1 & 8) != 0) {
    pcVar6 = pcVar6 + 1;
    bVar1 = (&DAT_00405541)[*pcVar6];
  }
  if (*pcVar6 == '-') {
    bVar3 = false;
    pcVar6 = pcVar6 + 1;
  }
  else {
    if (*pcVar6 == '+') {
      pcVar6 = pcVar6 + 1;
    }
    bVar3 = true;
  }
  pcVar7 = pcVar6;
  if (param_5 == 0) {
    param_5 = 10;
    if (*pcVar6 == '0') {
      param_5 = 8;
      iVar8 = 1;
      cVar2 = pcVar6[1];
      if ((cVar2 == 'x') || (cVar2 == 'X')) {
        param_5 = 0x10;
        iVar8 = 0;
        pcVar7 = pcVar6 + 2;
      }
      else if ((cVar2 == 'b') || (pcVar7 = pcVar6 + 1, cVar2 == 'B')) {
        param_5 = 2;
        iVar8 = 0;
        pcVar7 = pcVar6 + 2;
      }
    }
  }
  else {
    if (param_5 == 0x10) {
      if ((*pcVar6 != '0') || ((pcVar6[1] != 'x' && (pcVar6[1] != 'X')))) goto LAB_00404c5e;
    }
    else if ((param_5 != 2) || ((*pcVar6 != '0' || ((pcVar6[1] != 'b' && (pcVar6[1] != 'B'))))))
    goto LAB_00404c5e;
    pcVar7 = pcVar6 + 2;
  }
LAB_00404c5e:
  bVar4 = false;
  do {
    puVar5 = (undefined4 *)(int)*pcVar7;
    if ((*(byte *)((int)puVar5 + 0x405541) & 4) == 0) {
      if ((*(byte *)((int)puVar5 + 0x405541) & 3) == 0) {
LAB_00404d15:
        if (iVar8 == 0) {
          pcVar7 = param_3;
        }
        if (param_4 != (undefined4 *)0x0) {
          *param_4 = pcVar7;
          puVar5 = param_4;
        }
        if (((!bVar3) || (iVar9 != -0x80000000)) && (!bVar4)) {
          if (bVar3) {
            iVar9 = -iVar9;
          }
          return iVar9;
        }
        FUN_00404e64(puVar5,param_2,0x22);
        return (-(uint)!bVar3 & 1) + 0x7fffffff;
      }
      puVar5 = (undefined4 *)(((int)*pcVar7 & 0xffffffdfU) - 0x37);
    }
    else {
      puVar5 = puVar5 + -0xc;
    }
    if (param_5 <= (int)puVar5) goto LAB_00404d15;
    if (iVar9 < (int)(puVar5 + -0x20000000) / param_5) {
      bVar4 = true;
    }
    param_2 = param_5 * iVar9;
    iVar9 = param_2 - (int)puVar5;
    pcVar7 = pcVar7 + 1;
    iVar8 = iVar8 + 1;
  } while( true );
}



/* Address: 00404d1c */

/* WARNING: Globals starting with '_' overlap smaller symbols at the same address */

void __cdecl FUN_00404d1c(undefined4 *param_1)

{
  undefined4 uVar1;
  undefined4 *puVar2;
  
  LOCK();
  _DAT_00405a62 = _DAT_00405a62 + 1;
  UNLOCK();
  if (_DAT_00405a62 != 0) {
    FUN_00403c68(1);
  }
  puVar2 = param_1 + -1;
  if ((undefined4 *)0x3 < param_1 && puVar2 != (undefined4 *)0x0) {
    uVar1 = *(undefined4 *)*puVar2;
    *(undefined4 *)*puVar2 = puVar2;
    *param_1 = uVar1;
  }
  LOCK();
  _DAT_00405a62 = _DAT_00405a62 + -1;
  UNLOCK();
  if (-1 < _DAT_00405a62) {
    FUN_00403ce4(1);
  }
  return;
}



/* Address: 00404d5c */

void __cdecl FUN_00404d5c(undefined4 *param_1)

{
  undefined4 *puVar1;
  undefined4 uVar2;
  
  if ((undefined4 *)0x3 < param_1) {
    puVar1 = (undefined4 *)param_1[-1];
    uVar2 = *puVar1;
    *puVar1 = param_1 + -1;
    *param_1 = uVar2;
  }
  return;
}



/* Address: 00404d70 */

undefined4 * __cdecl FUN_00404d70(int param_1,int param_2)

{
  undefined4 *puVar1;
  
  puVar1 = FUN_00404800(param_1 * param_2);
  if (puVar1 == (undefined4 *)0x0) {
    return (undefined4 *)0x0;
  }
  FUN_00404700((undefined1 *)puVar1,0,param_1 * param_2);
  return puVar1;
}



/* Address: 00404dac */

LPVOID __cdecl FUN_00404dac(int param_1)

{
  LPVOID lpAddress;
  int *piVar1;
  
  lpAddress = VirtualAlloc((LPVOID)0x0,param_1 + 0xffffU & 0xffff0000,0x1000,4);
  if (lpAddress == (LPVOID)0x0) {
    return (LPVOID)0xffffffff;
  }
  if ((DAT_0040553c == (int *)0x0) || (0x3ff < DAT_0040553c[1])) {
    piVar1 = (int *)VirtualAlloc((LPVOID)0x0,0x1008,0x1000,4);
    if (piVar1 == (int *)0x0) {
      VirtualFree(lpAddress,0,0x8000);
      return (LPVOID)0xffffffff;
    }
    *piVar1 = (int)DAT_0040553c;
    DAT_0040553c = piVar1;
    piVar1[1] = 0;
  }
  DAT_0040553c[DAT_0040553c[1] + 2] = (int)lpAddress;
  DAT_0040553c[1] = DAT_0040553c[1] + 1;
  return lpAddress;
}



/* Address: 00404e54 */

int __cdecl FUN_00404e54(int param_1)

{
  return *(int *)(*(int *)(param_1 + -4) + 4) + -4;
}



/* Address: 00404e64 */

void __fastcall FUN_00404e64(undefined4 param_1,undefined4 param_2,undefined4 param_3)

{
  int iVar1;
  undefined8 uVar2;
  
  uVar2 = FUN_0040432e(param_1,param_2);
  iVar1 = (int)uVar2;
  if (iVar1 == 0) {
    return;
  }
  *(undefined4 *)(iVar1 + 0x10) = param_3;
  *(undefined4 *)(iVar1 + 0x8c) = 0;
  return;
}



/* 00404e98: RtlUnwind; Thunk to KERNEL32.DLL::RtlUnwind */

/* 00404e9e: GetStdHandle; Thunk to KERNEL32.DLL::GetStdHandle */

/* 00404ea4: WriteFile; Thunk to KERNEL32.DLL::WriteFile */

/* 00404eb0: ExitProcess; Thunk to KERNEL32.DLL::ExitProcess */

/* 00404ebc: GetCurrentThreadId; Thunk to KERNEL32.DLL::GetCurrentThreadId */

/* 00404ec2: FreeEnvironmentStringsA; Thunk to KERNEL32.DLL::FreeEnvironmentStringsA */

/* 00404ec8: GetEnvironmentStrings; Thunk to KERNEL32.DLL::GetEnvironmentStrings */

/* 00404f10: GetVersion; Thunk to KERNEL32.DLL::GetVersion */

/* 00404f3a: DdeUnaccessData; Thunk to USER32.DLL::DdeUnaccessData */

/* 00404f40: DdeAccessData; Thunk to USER32.DLL::DdeAccessData */

/* 00404f46: DdeFreeStringHandle; Thunk to USER32.DLL::DdeFreeStringHandle */

/* 00404f4c: DdeClientTransaction; Thunk to USER32.DLL::DdeClientTransaction */

/* 00404f52: DdeConnect; Thunk to USER32.DLL::DdeConnect */

/* 00404f58: DdeCreateStringHandleA; Thunk to USER32.DLL::DdeCreateStringHandleA */

/* 00404f5e: DdeDisconnect; Thunk to USER32.DLL::DdeDisconnect */

/* 00404f64: DdeFreeDataHandle; Thunk to USER32.DLL::DdeFreeDataHandle */

/* 00404f6a: DdeUninitialize; Thunk to USER32.DLL::DdeUninitialize */

/* 00404f70: DdeInitializeA; Thunk to USER32.DLL::DdeInitializeA */

