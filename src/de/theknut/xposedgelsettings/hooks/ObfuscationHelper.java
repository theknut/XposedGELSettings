package de.theknut.xposedgelsettings.hooks;

import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findClass;

public class ObfuscationHelper extends HooksBaseClass {

	public static class ClassNames {

		// class names to hook to
		public static String ALL_APPS_LIST,
		LAUNCHER,
		WORKSPACE,
		WORKSPACE_STATE,
		DEVICE_PROFILE,
		CELL_LAYOUT,
		CELL_LAYOUT_CELL_INFO,
		CELL_LAYOUT_LAYOUT_PARAMS,
		PAGED_VIEW,
		PAGED_VIEW_ICON,
		PAGED_VIEW_CELL_LAYOUT,
		PAGED_VIEW_WITH_DRAGGABLE_ITEMS,
		APPS_CUSTOMIZE_CONTENT_TYPE,
		APPS_CUSTOMIZE_CELL_LAYOUT,
		APPS_CUSTOMIZE_LAYOUT,
		APPS_CUSTOMIZE_PAGED_VIEW,
		APPS_CUSTOMIZE_TAB_HOST,
		WALLPAPEROFFSETINTERPOLATOR,
		WALLPAPER_CROP_ACTIVITY,
		FOLDER,
		FOLDER_ICON,
		HOTSEAT,
		DRAG_SOURCE,
		ITEM_INFO,
		APP_INFO,
		SHORTCUT_INFO,
		SEARCH_DROP_TARGET_BAR,
		ICON_CACHE,
		UTILITIES,
		CASH_ENTRY,
		CELL_INFO,
		LOADER_TASK,
		FOLDER_INFO,
        LAUNCHER_MODEL,
        APP_WIDGET_RESIZE_FRAME,
        ITEM_CONFIGURATION,
		GEL,
		NOW_OVERLAY,
		SEARCH_OVERLAY_IMPL,
		SEARCH_PLATE;
		
		public static String oALL_APPS_LIST,
		oLAUNCHER,
		oWORKSPACE,
		oWORKSPACE_STATE,
		oDEVICE_PROFILE,
		oCELL_LAYOUT,
		oCELL_LAYOUT_CELL_INFO,
		oCELL_LAYOUT_LAYOUT_PARAMS,
		oPAGED_VIEW,
		oPAGED_VIEW_ICON,
		oPAGED_VIEW_CELL_LAYOUT,
		oPAGED_VIEW_WITH_DRAGGABLE_ITEMS,
		oAPPS_CUSTOMIZE_CONTENT_TYPE,
		oAPPS_CUSTOMIZE_CELL_LAYOUT,
		oAPPS_CUSTOMIZE_LAYOUT,
		oAPPS_CUSTOMIZE_PAGED_VIEW,
		oAPPS_CUSTOMIZE_TAB_HOST,
		oWALLPAPEROFFSETINTERPOLATOR,
		oWALLPAPER_CROP_ACTIVITY,
		oFOLDER,
		oFOLDER_ICON,
		oHOTSEAT,
		oDRAG_SOURCE,
		oITEM_INFO,
		oAPP_INFO,
		oSHORTCUT_INFO,
		oICON_CACHE,
		oUTILITIES,
		oCASH_ENTRY,
		oCELL_INFO,
		oLOADER_TASK,
		oFOLDER_INFO,
		oSEARCH_DROP_TARGET_BAR,
        oLAUNCHER_MODEL,
        oAPP_WIDGET_RESIZE_FRAME,
        oITEM_CONFIGURATION,
		oGEL,
		oNOW_OVERLAY,
		oSEARCH_OVERLAY_IMPL,
		oSEARCH_PLATE;
		
		public static void initNames(String launcherName, boolean first) {

			String launcherPackage = "com.android." + launcherName + ".";
			ALL_APPS_LIST = launcherPackage + "AllAppsList";
			ITEM_INFO = launcherPackage + "ItemInfo";
			APP_INFO = launcherPackage + "AppInfo";
			CELL_LAYOUT = launcherPackage + "CellLayout";
            CELL_LAYOUT_CELL_INFO = CELL_LAYOUT + "$CellInfo";
			CELL_LAYOUT_LAYOUT_PARAMS = launcherPackage + "CellLayout$LayoutParams";
			SEARCH_DROP_TARGET_BAR = launcherPackage + "SearchDropTargetBar";
			LAUNCHER = launcherPackage + "Launcher";
			PAGED_VIEW = launcherPackage + "PagedView";
			PAGED_VIEW_CELL_LAYOUT = launcherPackage + "PagedViewCellLayout";
			PAGED_VIEW_WITH_DRAGGABLE_ITEMS = launcherPackage + "PagedViewWithDraggableItems";
			APPS_CUSTOMIZE_PAGED_VIEW = launcherPackage + "AppsCustomizePagedView";
			APPS_CUSTOMIZE_LAYOUT = launcherPackage + "AppsCustomizeLayout";
			APPS_CUSTOMIZE_CELL_LAYOUT = launcherPackage + "AppsCustomizeCellLayout";
			PAGED_VIEW_ICON = launcherPackage + "PagedViewIcon";
			DEVICE_PROFILE = launcherPackage + "DeviceProfile";
			WORKSPACE = launcherPackage + "Workspace";
			WALLPAPEROFFSETINTERPOLATOR = WORKSPACE + "$WallpaperOffsetInterpolator";
			APPS_CUSTOMIZE_TAB_HOST = launcherPackage + "AppsCustomizeTabHost";
			APPS_CUSTOMIZE_CONTENT_TYPE = APPS_CUSTOMIZE_PAGED_VIEW + "$ContentType";
			WALLPAPER_CROP_ACTIVITY = launcherPackage + "WallpaperCropActivity";
			FOLDER = launcherPackage + "Folder";
			FOLDER_ICON = launcherPackage + "FolderIcon";
			HOTSEAT = launcherPackage + "Hotseat";
			DRAG_SOURCE = launcherPackage + "DragSource";
			SHORTCUT_INFO = launcherPackage + "ShortcutInfo";
			ICON_CACHE = launcherPackage + "IconCache";
			UTILITIES = launcherPackage + "Utilities";
			CASH_ENTRY = launcherPackage + "IconCache$CacheEntry";
			LOADER_TASK = launcherPackage + "LauncherModel$LoaderTask";
			FOLDER_INFO = launcherPackage + "FolderInfo";
            LAUNCHER_MODEL = launcherPackage + "LauncherModel";
            APP_WIDGET_RESIZE_FRAME = launcherPackage + "AppWidgetResizeFrame";
            ITEM_CONFIGURATION = CELL_LAYOUT + "$ItemConfiguration";

			GEL = "com.google.android.launcher.GEL";
			NOW_OVERLAY = "com.google.android.sidekick.shared.client.NowOverlay";
			SEARCH_OVERLAY_IMPL = "com.google.android.search.gel.SearchOverlayImpl";
			//SEARCH_PLATE = "com.google.android.search.shared.ui.SearchPlate";

			oALL_APPS_LIST = "nz"; // kp !!!!!!!!!!!!!!!!!!!!
			oITEM_INFO = first ? "ta" : "pr"; //pr
			oAPP_INFO = first ? "ob" : "kr"; //kr
			oCELL_LAYOUT = launcherPackage + "CellLayout";
			oSEARCH_DROP_TARGET_BAR = launcherPackage + "SearchDropTargetBar";
			oDEVICE_PROFILE = first ? "qi" : "mz"; //mz
			oLAUNCHER = launcherPackage + "Launcher";
			oPAGED_VIEW = launcherPackage + "PagedView";
			oPAGED_VIEW_CELL_LAYOUT = first ? "yo" : "vd"; //vd
			oPAGED_VIEW_WITH_DRAGGABLE_ITEMS = first ? "yw" : "vl"; //vl
			oAPPS_CUSTOMIZE_PAGED_VIEW = launcherPackage + "AppsCustomizePagedView";
			oAPPS_CUSTOMIZE_CELL_LAYOUT = first ? "yr" : "kw"; //kw
			oPAGED_VIEW_ICON = launcherPackage + "PagedViewIcon";
			oWORKSPACE = launcherPackage + "Workspace";
			oWALLPAPEROFFSETINTERPOLATOR = first ? "acp" : "zd"; //zd
			oAPPS_CUSTOMIZE_TAB_HOST = launcherPackage + "AppsCustomizeTabHost";
			oAPPS_CUSTOMIZE_CONTENT_TYPE = first ? "oo" : "lf"; //lf
			oFOLDER = launcherPackage + "Folder";
			oFOLDER_ICON = launcherPackage + "FolderIcon";
			oCELL_LAYOUT_LAYOUT_PARAMS = launcherPackage + "CellLayout$LayoutParams";
			oCELL_LAYOUT_CELL_INFO = first ? "pi" : "lz"; //lz
			oWORKSPACE_STATE = first ? "aco" : "zc"; //zc
			oHOTSEAT = launcherPackage + "Hotseat";
			oDRAG_SOURCE = first ? "qw" : "nn"; // first parameter in onDragStart of SearchDropTargetBar // nn
			oSHORTCUT_INFO = first ? "zl" : "vz"; //vz
			oICON_CACHE = first ? "ss" : "pk"; //pk
			oUTILITIES = first ? "zu" : "wi"; //wi
			oCASH_ENTRY = first ? "st" : "pl"; //pl
			oLOADER_TASK = first ? "wl" : "tb"; //tb
			oFOLDER_INFO = first ? "sh" : "oz"; //oz
            oLAUNCHER_MODEL = first ? "vq" : "sg"; //sg
            oAPP_WIDGET_RESIZE_FRAME = first ? "oc" : "ks"; //ks
            oITEM_CONFIGURATION = first ? "pj" : "ma"; //ma

			oGEL = "com.google.android.launcher.GEL";
			oNOW_OVERLAY = first ? "enc" : "dzk"; //dzk
			oSEARCH_OVERLAY_IMPL = first ? "cmh" : "ccu"; //ccu
			//oSEARCH_PLATE = "com.google.android.search.searchplate.SearchPlate";
		}
	}
	
	public static class Classes {

		static public Class<?> Launcher,
		Workspace,
		AppInfo,
		AppsCustomizePagedView,
		CellLayout,
		WallpaperOffsetInterpolator,
		PagedViewIcon,
		DeviceProfile,
		AppsCustomizeLayout,
		AppsCustomizeTabHost,
		AllAppsList,
		Folder,
		FolderIcon,
		PagedViewWithDraggableItems,
		PagedView,
		GELClass,
		NowOverlay,
		SearchOverlayImpl,
		//SearchPlate,
		SearchDropTargetBar,
		DragSource,
		CellLayoutLayoutParams,
		CellLayoutCellInfo,
		WorkspaceState,
		Hotseat,
		AppsCustomizeContentType,
		ShortcutInfo,
		IconCache,
		Utilities,
		CacheEntry,
		CellInfo,
		ItemInfo,
		LoaderTask,
		FolderInfo,
        LauncherModel,
        AppWidgetResizeFrame,
        ItemConfiguration,
        ADL;

		public static void hookAllClasses(LoadPackageParam lpparam) {
			try {
				Launcher = findClass(ClassNames.LAUNCHER, lpparam.classLoader);
				Workspace = findClass(ClassNames.WORKSPACE, lpparam.classLoader);
				AppsCustomizePagedView = findClass(ClassNames.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
				CellLayout = findClass(ClassNames.CELL_LAYOUT, lpparam.classLoader);
				CellLayoutLayoutParams = findClass(ClassNames.CELL_LAYOUT_LAYOUT_PARAMS, lpparam.classLoader);
				WallpaperOffsetInterpolator = findClass(ClassNames.WALLPAPEROFFSETINTERPOLATOR, lpparam.classLoader);
				PagedViewIcon = findClass(ClassNames.PAGED_VIEW_ICON, lpparam.classLoader);
				DeviceProfile = findClass(ClassNames.DEVICE_PROFILE, lpparam.classLoader);
				AppInfo = findClass(ClassNames.APP_INFO, lpparam.classLoader);

				if (Common.HOOKED_PACKAGE.equals(Common.TREBUCHET_PACKAGE)) {
					AppsCustomizeLayout = findClass(ClassNames.APPS_CUSTOMIZE_LAYOUT, lpparam.classLoader);
				} else {
					AppsCustomizeTabHost = findClass(ClassNames.APPS_CUSTOMIZE_TAB_HOST, lpparam.classLoader);
				}

                CellLayoutCellInfo = findClass(ClassNames.CELL_LAYOUT_CELL_INFO, lpparam.classLoader);
				AppsCustomizeContentType = findClass(ClassNames.APPS_CUSTOMIZE_CONTENT_TYPE, lpparam.classLoader);
				AllAppsList = findClass(ClassNames.ALL_APPS_LIST, lpparam.classLoader);
				Folder = findClass(ClassNames.FOLDER, lpparam.classLoader);
				PagedViewWithDraggableItems = findClass(ClassNames.PAGED_VIEW_WITH_DRAGGABLE_ITEMS, lpparam.classLoader);
				PagedView = findClass(ClassNames.PAGED_VIEW, lpparam.classLoader);
				FolderIcon = findClass(ClassNames.FOLDER_ICON, lpparam.classLoader);
				Hotseat = findClass(ClassNames.HOTSEAT, lpparam.classLoader);
				DragSource = findClass(ClassNames.DRAG_SOURCE, lpparam.classLoader);
				ShortcutInfo = findClass(ClassNames.SHORTCUT_INFO, lpparam.classLoader);
				SearchDropTargetBar = findClass(ClassNames.SEARCH_DROP_TARGET_BAR, lpparam.classLoader);
				IconCache = findClass(ClassNames.ICON_CACHE, lpparam.classLoader);
				Utilities = findClass(ClassNames.UTILITIES, lpparam.classLoader);
				CacheEntry = findClass(ClassNames.CASH_ENTRY, lpparam.classLoader);
				ItemInfo = findClass(ClassNames.ITEM_INFO, lpparam.classLoader);
				LoaderTask = findClass(ClassNames.LOADER_TASK, lpparam.classLoader);
				FolderInfo = findClass(ClassNames.FOLDER_INFO, lpparam.classLoader);
                LauncherModel = findClass(ClassNames.LAUNCHER_MODEL, lpparam.classLoader);
                AppWidgetResizeFrame = findClass(ClassNames.APP_WIDGET_RESIZE_FRAME, lpparam.classLoader);
                ItemConfiguration = findClass(ClassNames.ITEM_CONFIGURATION, lpparam.classLoader);

				if (lpparam.packageName.equals(Common.GEL_PACKAGE)) {
					GELClass = findClass(ClassNames.GEL, lpparam.classLoader);
					NowOverlay = findClass(ClassNames.NOW_OVERLAY, lpparam.classLoader);
					SearchOverlayImpl = findClass(ClassNames.SEARCH_OVERLAY_IMPL, lpparam.classLoader);
					//SearchPlate = findClass(SEARCH_PLATE, lpparam.classLoader);					
				}

				if (PreferencesHelper.Debug) log("Hooking non-obfuscated GNL");
			} catch (ClassNotFoundError cnfe) {
				if (PreferencesHelper.Debug) {
					log("Upsi! " + cnfe);
					log("Couldn't hook classes, trying obfuscated classes now");
				}

				try {
					if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE))	{
						Launcher = findClass(ClassNames.oLAUNCHER, lpparam.classLoader);
						Workspace = findClass(ClassNames.oWORKSPACE, lpparam.classLoader);
						AppsCustomizePagedView = findClass(ClassNames.oAPPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
						CellLayout = findClass(ClassNames.oCELL_LAYOUT, lpparam.classLoader);
						WallpaperOffsetInterpolator = findClass(ClassNames.oWALLPAPEROFFSETINTERPOLATOR, lpparam.classLoader);
						PagedViewIcon = findClass(ClassNames.oPAGED_VIEW_ICON, lpparam.classLoader);
						DeviceProfile = findClass(ClassNames.oDEVICE_PROFILE, lpparam.classLoader);
						AppsCustomizeTabHost = findClass(ClassNames.oAPPS_CUSTOMIZE_TAB_HOST, lpparam.classLoader);
						AppsCustomizeContentType = findClass(ClassNames.oAPPS_CUSTOMIZE_CONTENT_TYPE, lpparam.classLoader);
						AllAppsList = findClass(ClassNames.oALL_APPS_LIST, lpparam.classLoader);
						Folder = findClass(ClassNames.oFOLDER, lpparam.classLoader);
						PagedViewWithDraggableItems = findClass(ClassNames.oPAGED_VIEW_WITH_DRAGGABLE_ITEMS, lpparam.classLoader);
						PagedView = findClass(ClassNames.oPAGED_VIEW, lpparam.classLoader);
						FolderIcon = findClass(ClassNames.oFOLDER_ICON, lpparam.classLoader);
						CellLayoutLayoutParams = findClass(ClassNames.oCELL_LAYOUT_LAYOUT_PARAMS, lpparam.classLoader);
						CellLayoutCellInfo = findClass(ClassNames.oCELL_LAYOUT_CELL_INFO, lpparam.classLoader);
						WorkspaceState = findClass(ClassNames.oWORKSPACE_STATE, lpparam.classLoader);
						Hotseat = findClass(ClassNames.oHOTSEAT, lpparam.classLoader);
						AppInfo = findClass(ClassNames.oAPP_INFO, lpparam.classLoader);
						ShortcutInfo = findClass(ClassNames.oSHORTCUT_INFO, lpparam.classLoader);
						DragSource = findClass(ClassNames.oDRAG_SOURCE, lpparam.classLoader);
						SearchDropTargetBar = findClass(ClassNames.oSEARCH_DROP_TARGET_BAR, lpparam.classLoader);
						IconCache = findClass(ClassNames.oICON_CACHE, lpparam.classLoader);
						Utilities = findClass(ClassNames.oUTILITIES, lpparam.classLoader);
						CacheEntry = findClass(ClassNames.oCASH_ENTRY, lpparam.classLoader);
						ItemInfo = findClass(ClassNames.oITEM_INFO, lpparam.classLoader);
						LoaderTask = findClass(ClassNames.oLOADER_TASK, lpparam.classLoader);
						FolderInfo = findClass(ClassNames.oFOLDER_INFO, lpparam.classLoader);
                        LauncherModel = findClass(ClassNames.oLAUNCHER_MODEL, lpparam.classLoader);
                        AppWidgetResizeFrame = findClass(ClassNames.oAPP_WIDGET_RESIZE_FRAME, lpparam.classLoader);
                        ItemConfiguration = findClass(ClassNames.oITEM_CONFIGURATION, lpparam.classLoader);
                        ADL = findClass("adl", lpparam.classLoader);
						Common.PACKAGE_OBFUSCATED = true;

						GELClass = findClass(ClassNames.oGEL, lpparam.classLoader);
						NowOverlay = findClass(ClassNames.oNOW_OVERLAY, lpparam.classLoader);
						SearchOverlayImpl = findClass(ClassNames.oSEARCH_OVERLAY_IMPL, lpparam.classLoader);
						//SearchPlate = findClass(oSEARCH_PLATE, lpparam.classLoader);
					}					

					if (PreferencesHelper.Debug) log("Hooking obfuscated GNL");
				} catch (ClassNotFoundError cnfe2) {
					log("OH SNAP! It looks like GNL got updated and is no longer compatible. Please sit tight, I'm probably already working on it!");					
				}
			}
		}
	}

	public static class Fields {

		public static String hotseatAllAppsRank,
		dpNumHotseatIcons,
		itemInfoTitle,
		celllayoutlayoutparamsCanReorder,
		deviceProfileSearchBarSpaceWidthPx,
		launcherSearchDropTargetBar,
		launcherQsbBar,
		sdtbIsSearchBarHidden,
		sdtbQsbBar,
		workspaceCustomContentShowing,
		workspaceCurrentPage,
		launcherHotseat,
		launcherAppsCustomizeTabHost,
		acthInTransition,
		workspaceState,
		workspaceDefaultPage,
		bubbleTextView,
		folderIcon,
		btvShadowsEnabled,
		fiPreviewBackground,
		fiFolderName,
		fiFolderEditText,
		fiFolder,
		fiFolderIcon,
		fFolderIcon,
		acpvContentType,
		pvIsPageMoving,
		wIsSwitchingState,
		dpHotseatBarHeightPx,
		lState,
		cellInfoClass,
		shortcutInfoClass,
		folderInfoClass,
		wTouchState,
		pvNextPage,
		lHasFocus,
		lPaused,
		iiItemType,
		dpSearchBarHeightPx,
		dpSearchBarSpaceHeightPx,
		dpSearchBarSpaceWidthPx,
		aiComponentName,
		acpvCurrentPage,
		acpvAllAppsNumCols,
		acpvAllAppsNumRows,
		pvPageIndicator,
		acthContent,
		pagedViewIcon,
		dpPageIndicatorHeightPx,
		dpDesiredWorkspaceLeftRightMarginPx,
		fContent,
		lAppsCustomizePagedView,
		iiTitle,
		iiID,
		uSCanvas,
		ceIcon,
        ceTitle,
		icIconDensity,
		ciCell,
		lAppWidgetHostView,
		wDragInfo,
		LauncherAppWidgetInfo,
        lIconCache,
        iiCellX,
        iiCellY,
        iiSpanX,
        iiSpanY,
        awrfWidgetView,
        fiLongPressHelper,
        clphHasPerformedLongPress;

		public static void initFieldNames(boolean first) {

			if (Common.PACKAGE_OBFUSCATED) {
				hotseatAllAppsRank = first ? "BQ" : "zp"; // only / 2 operation // zp
				dpNumHotseatIcons = first ? "AY" : "yz"; // toString of DynamicGrid // yz
				itemInfoTitle = "title";
				celllayoutlayoutparamsCanReorder = first ? "yE" : "wf"; // second member with = true // wf
				deviceProfileSearchBarSpaceWidthPx = "zs";
				launcherSearchDropTargetBar = "um";
				launcherQsbBar = "EU";
				sdtbIsSearchBarHidden = first ? "PF" : "MV"; // above Qsb member // MV
				sdtbQsbBar = first ? "PG" : "MW"; // MW
				workspaceCustomContentShowing = first ? "SH" : "PV"; // setContentDescription // PV
				workspaceCurrentPage = acpvCurrentPage = first ? "Nm" : "KF"; // setPadding(0, 0, 0, 0); // KF
				launcherHotseat = first ? "Hu" : "EO"; // EO
				launcherAppsCustomizeTabHost = first ? "Hx" : "ER"; // ER
				acthInTransition = first ? "vF" : "tf"; // onInterceptTouchEvent first member in if-clause // tf
				workspaceState = first ? "SV" : "Qj"; // WorkspaceState member //  Qj
				workspaceDefaultPage = first ? "Su" : "PI"; // "Expected custom content", member gets decreased by one // PI
				bubbleTextView = "BubbleTextView";
				folderIcon = "FolderIcon";
				pagedViewIcon = "PagedViewIcon";
				btvShadowsEnabled = first ? "wF" : "ue"; // only boolean member = true// ue
				fiPreviewBackground = first ? "Fh" : "CE"; // only ImageView member // CE
				fiFolderName = first ? "Fi" : "CF"; // only BubbleTextView // CF
				fiFolderEditText = first ? "EJ" : "Cf"; // only FolderEditText member // Cf
				fiFolder = first ? "Fe" : "CB"; // only Folder member // CB
				fiFolderIcon = "FolderIcon";
				fFolderIcon = first ? "Ep" : "BL"; // only FolderIcon member // BL
				acpvContentType = first ? "uW" : "sw"; // private oo uW = oo.vW;// sw
				pvIsPageMoving = first ? "Oc" : "Lv"; // in pageBeginMoving if-clause // Lv
				pvNextPage = first ? "Np" : "KI"; // String.format // KI
				dpHotseatBarHeightPx = first ? "BP" : "zo"; // 4 * ... // zo
				lState = first ? "GZ" : "Et"; // onNewIntent if-clause after Folder // Et
				cellInfoClass = first ? "CellLayout" : "lz"; // lz
				shortcutInfoClass = first ? "zl" : "vz"; // vz
				folderInfoClass = first ? "sh" : "oz"; // oz
				wTouchState = first ? "NF" : "KY"; // onInterceptTouchEvent while clause// KY
				lHasFocus = first ? "HP" : "Fj"; // onWindowFocusChanged // Fj
				lPaused = first ? "HF" : "EZ"; // only boolean assignement in onPause() // EZ
				iiItemType = first ? "GT" : "En"; // Item(id= // En
				dpSearchBarHeightPx = "zv";
				dpSearchBarSpaceHeightPx = "zu";
				dpSearchBarSpaceWidthPx = "zs";
				aiComponentName = first ? "uj" : "rJ"; // only ComponentName member // rJ
				acpvAllAppsNumCols = first ? "BS" : "zr"; // onMeasure localDeviceProfile // zr
				acpvAllAppsNumRows = first ? "BR" : "zq"; // onMeasure localDeviceProfile // zq
				pvPageIndicator = first ? "Og" : "Lz"; // setContentDescription // Lz
				acthContent = first ? "wd" : "tD"; // .getLayoutParams in setInsets // tD
				dpPageIndicatorHeightPx = first ? "BX" : "zw"; // last parameter in .set // zw
				dpDesiredWorkspaceLeftRightMarginPx = "yJ";
				wIsSwitchingState = first ? "SW" : "Qk"; // start from onTouch, second method call in if-clause // Qk
				fContent = first ? "El" : "BH"; // only CellLayout member // BH
				lAppsCustomizePagedView = first ? "Hy" : "ES"; // AppsCustomizePagedView in Launcher // ES
				iiID = "id";
				iiTitle = "title";
				uSCanvas = "NL";
				ceIcon = first ? "GE" : "DZ"; // DZ
                ceTitle = "title";
				icIconDensity = "DY";
				ciCell = "vL";
				lAppWidgetHostView = first ? "ve" : "ru"; // make logging // ru
				wDragInfo = "PP";
				LauncherAppWidgetInfo = first ? "vf" : "rv"; // AppWidget(id= // rv
                lIconCache = first ? "uf" : "rF"; // IconCache member in Launcher // rF
                iiCellX = "vM";
                iiCellY = "vN";
                iiSpanX = "vJ";
                iiSpanY = "vK";
                awrfWidgetView = first ? "uk" : "rK"; // only obfuscated member class // rK
                fiLongPressHelper = first ? "wJ" : "ui"; // cancelLongPress // ui
                clphHasPerformedLongPress = first ? "zf" : "wG"; // only boolean member // wG
			} else {
				hotseatAllAppsRank = "hotseatAllAppsRank";
				dpNumHotseatIcons = "numHotseatIcons";
				itemInfoTitle = "title";
				celllayoutlayoutparamsCanReorder = "canReorder";
				deviceProfileSearchBarSpaceWidthPx = "searchBarSpaceWidthPx";
				launcherSearchDropTargetBar = "mSearchDropTargetBar";
				launcherQsbBar = "mQsbBar";
				sdtbIsSearchBarHidden = "mIsSearchBarHidden";
				sdtbQsbBar = "mQSBSearchBar";
				workspaceCustomContentShowing = "mCustomContentShowing";
				workspaceCurrentPage = acpvCurrentPage = "mCurrentPage";
				launcherHotseat = "mHotseat";
				launcherAppsCustomizeTabHost = "mAppsCustomizeTabHost";
				acthInTransition = "mInTransition";
				workspaceState = "mState";
				workspaceDefaultPage = "mDefaultPage";
				bubbleTextView = "BubbleTextView";
				folderIcon = "FolderIcon";
				pagedViewIcon = "PagedViewIcon";
				btvShadowsEnabled = "mShadowsEnabled";
				fiPreviewBackground = "mPreviewBackground";
				fiFolderName = "mFolderName";
				fiFolder = "mFolder";
				fiFolderIcon = "FolderIcon";
				fFolderIcon = "mFolderIcon";
				fiFolderEditText = "mFolderName";
				acpvContentType = "mContentType";
				pvIsPageMoving = "mIsPageMoving";
				dpHotseatBarHeightPx = "hotseatBarHeightPx";
				lState = "mState";
				cellInfoClass = "CellInfo";
				shortcutInfoClass = "ShortcutInfo";
				folderInfoClass = "FolderInfo";
				wTouchState = "mTouchState";
				pvNextPage = "mNextPage";
				lHasFocus = "mHasFocus";
				lPaused = "mPaused";
				iiItemType = "itemType";
				dpSearchBarHeightPx = "searchBarHeightPx";
				dpSearchBarSpaceHeightPx = "searchBarSpaceHeightPx";
				aiComponentName = "componentName";
				acpvAllAppsNumCols = "allAppsNumCols";
				acpvAllAppsNumRows = "allAppsNumRows";
				pvPageIndicator = "mPageIndicator";
				acthContent = "mContent";
				dpPageIndicatorHeightPx = "pageIndicatorHeightPx";
				dpDesiredWorkspaceLeftRightMarginPx = "desiredWorkspaceLeftRightMarginPx";
				wIsSwitchingState = "mIsSwitchingState";
				fContent = "mContent";
				lAppsCustomizePagedView = "mAppsCustomizeContent";
				iiID = "id";
				iiTitle = "title";
				dpSearchBarSpaceWidthPx = "searchBarSpaceWidthPx";
				uSCanvas = "sCanvas";
				ceIcon = "icon";
                ceTitle = "title";
				icIconDensity = "mIconDpi";
				ciCell = "cell";
				lAppWidgetHostView = "LauncherAppWidgetHostView";
				wDragInfo = "mDragInfo";
				LauncherAppWidgetInfo = "LauncherAppWidgetInfo";
                lIconCache = "mIconCache";
                iiCellX = "cellX";
                iiCellY = "cellY";
                iiSpanX = "spanX";
                iiSpanY = "spanY";
                awrfWidgetView = "mWidgetView";
                fiLongPressHelper = "mLongPressHelper";
                clphHasPerformedLongPress = "mHasPerformedLongPress";
			}
		}
	}

	public static class Methods {

		public static String applyFromApplicationInfo,
		itemInfoTitle,
		launcherOnCreate,
		launcherGetApplicationContext,
		launcherIsRotationEnabled,
		celllayoutAddViewToCellLayout,
		wallpaperoffsetinterpolatorSyncWithScroll,
		workspaceStartDrag,
		workspaceBeginDraggingApplication,
		workspaceBeginDragShared,
		acpvBeginDraggingApplication,
		acpvOnPackagesUpdated,
		dynamicgridLayout,
		launcherGetSearchbar,
		launcherGetQsbBar,
		pagedviewPageBeginMoving,
		pagedviewPageEndMoving,
		sdtbOnDragStart,
		sdtbOnDragEnd,
		launcherHasCustomContentToLeft,
		hideAppsCustomizeHelper,
		launcherShowWorkspace,
		launcherShowAllApps,
		workspaceMoveToDefaultScreen,
		btvSetShadowsEnabled,
		wsOverScroll,
		lFinishBindingItems,
		dpGetWorkspacePadding,
		lIsAllAppsVisible,
		lGetOpenFolder,
		wIsOnOrMovingToCustomContent,
		wEnterOverviewMode,
		wMoveToCustomContentScreen,
		pvSnapToPage,
		lOpenFolder,
		wOnDragEnd,
		wOnDragStart,
		lCloseFolder,
		acthOnTabChanged,
		wSetCurrentPage,
		acpvOverScroll,
		acpvSetCurrentPage,
		aalAdd,
		dpUpdateFromConfiguration,
		lHideAppsCustomizeHelper,
		acthSetInsets,
		wSnapToPage,
		soiSetSearchStarted,
		noOnShow,
		wOnLauncherTransitionEnd,
		fOnRemove,
		fOnAdd,
		fReplaceFolderWithFinalItem,
		fGetItemsInReadingOrder,
		clGetShortcutsAndWidgets,
		acthGetContentTypeForTabTag,
		lStartActivitySafely,
		wOnTransitionPrepare,
		siGetIntent,
		icGetFullResIcon,
		uCreateIconBitmap,
		icCacheLocked,
		clMarkCellsForView,
		wStartDrag,
		lmCheckItemPlacement,
        acpvBeginDragging,
        lBindAppsUpdated,
        lmIsShortcutInfoUpdateable,
        awrfCommitResize,
        clAttemptPushInDirection,
        acpvSetApps,
        acpvUpdateApps,
        acpvRemoveApps;

		public static void initMethodNames(boolean first) {

			if (Common.PACKAGE_OBFUSCATED) {
				applyFromApplicationInfo = "a";
				itemInfoTitle = "title";
				launcherOnCreate = "onCreate";
				launcherGetApplicationContext = "getApplicationContext";
				launcherIsRotationEnabled = first ? "hr" : "gC"; // getBoolean - single line method // gC
				celllayoutAddViewToCellLayout = "a"; // View paramView, int paramInt1, int paramInt2, CellLayout.LayoutParams paramLayoutParams, boolean paramBoolean
				wallpaperoffsetinterpolatorSyncWithScroll = first ? "la" : "kf"; // computeScroll in Workspace // kf
				workspaceStartDrag = "a"; // isInTouchMode
				workspaceBeginDraggingApplication = "P";
				workspaceBeginDragShared = "a";
				acpvOnPackagesUpdated = "a"; // "can not fit on this device"
				dynamicgridLayout = "a";
				launcherGetSearchbar = first ? "gO" : "fZ"; // return  SearchDropTargetBar in Launcher // fZ
				launcherGetQsbBar = first ? "hl" : "gw"; // inflate followed by addView // gw
				pagedviewPageBeginMoving = first ? "iY" : "ii"; // protected void // ii
				pagedviewPageEndMoving = first ? "iZ" : "ij";  // ij
				sdtbOnDragStart = "a"; // twice .start in the method
				sdtbOnDragEnd = first ? "ei" : "dt"; // twice .reverse // dt
				launcherHasCustomContentToLeft = first ? "gA" : "fL"; // isEmpty // fL
				hideAppsCustomizeHelper = "a"; //
				launcherShowWorkspace = "a"; // boolean paramBoolean, Runnable paramRunnable
				launcherShowAllApps = "a";
				workspaceMoveToDefaultScreen = first ? "at" : "ao"; // Launcher onNewIntent method call of workspace member with (true) // ao
				btvSetShadowsEnabled = first ? "z" : "w"; // w
				wsOverScroll = acpvOverScroll = "g"; // (float paramFloat)
				lFinishBindingItems = first ? "Z" : "U"; // hasFocus() // U
				dpGetWorkspacePadding = first ? "aS" : "aC"; // second method with (int paramInt)  // aC
				lIsAllAppsVisible = first ? "hh" : "gs"; // onBackPressed first method call // gs
				lGetOpenFolder = first ? "kj" : "jp"; // in closeFolder // jp
				wIsOnOrMovingToCustomContent = first ? "kE" : "jJ"; // last if-clause in Launcher onResume// jJ
				wEnterOverviewMode = first ? "kJ" : "jO"; // Touchstate != 0 // jO
				wMoveToCustomContentScreen = first ? "au" : "ap"; // Workspace "View localView = getChildAt" // ap
				pvSnapToPage = "a"; // int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, TimeInterpolator paramTimeInterpolator
				lOpenFolder = "i"; // "Folder info marked as ope" // i
				lCloseFolder = first ? "hg" : "gr"; // gr
				acthOnTabChanged = "c"; // setBackgroundColor
				wSetCurrentPage = acpvSetCurrentPage = first ? "bl" : "aV"; // aV
				aalAdd = "a";
				dpUpdateFromConfiguration = "a"; // float paramFloat, int paramInt, Resources paramResources, DisplayMetrics paramDisplayMetrics
				lHideAppsCustomizeHelper = "a";
				acthSetInsets = "c"; // (Rect
				wSnapToPage = first ? "bs" : "bc"; // in PagedView requestChildFocus// bc
				soiSetSearchStarted = first ? "cI" : "cs"; // onResume before cancel() // cs
				noOnShow = first ? "u" : "p"; // boolean paramBoolean1, boolean paramBoolean2 // p
				wOnDragStart = "a"; // only method with interface parameters with InstallShortcutReceiver
				wOnDragEnd = first ? "ei" : "dt"; // only method without interface parameters with InstallShortcutReceiver // dt
				wOnLauncherTransitionEnd = "a"; // , boolean paramBoolean1, boolean paramBoolean2)
				fOnRemove = "g"; // removeView(localView)
				fOnAdd = "f"; // (<Shortcutinfo> param<...>)
				fReplaceFolderWithFinalItem = "ge"; // getItemCount() <= 1
				fGetItemsInReadingOrder = first ? "gh" : "fr"; // public final ArrayList // fr
				clGetShortcutsAndWidgets = first ? "ew" : "dH"; // getChildCount() > 0 // dH
				acthGetContentTypeForTabTag = first ? "r" : "j"; // (String paramString) // j
				lStartActivitySafely = "b";
				wOnTransitionPrepare = first ? "kM" : "jR"; // boolean bool = true; this.<IsSwitchingState> = bool; // jR
				siGetIntent = "getIntent";
				icGetFullResIcon = "a"; // (Resources paramResources, int paramInt)
				uCreateIconBitmap = "a"; // (Drawable paramDrawable, Context paramContext)
				icCacheLocked = first ? "a" : "b"; // b
				clMarkCellsForView = "a"; // int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean[][] paramArrayOfBoolean, boolean paramBoolean
				wStartDrag = "b";
				lmCheckItemPlacement = "a"; // "Error loading shortcut into "
                acpvBeginDragging = "n"; // "instanceof PagedViewIcon" in AppsCustomizePagedView // n
                lBindAppsUpdated = "l"; // if (this.Hi == null)
                lmIsShortcutInfoUpdateable = "e"; // "android.intent.action.MAIN" // e
                awrfCommitResize = first ? "du" : "cF"; // requestLayout // cF
                clAttemptPushInDirection = "b"; // ArrayList paramArrayList, Rect paramRect, int[] paramArrayOfInt, View paramView, pj parampj
                acpvSetApps = "b"; // Collections.sort // b
                acpvUpdateApps = "g"; // in BindAppsUpdated in Launcher //
                acpvRemoveApps = "f"; // in Launcher (paramArrayList2)
			} else {
				applyFromApplicationInfo = "applyFromApplicationInfo";
				itemInfoTitle = "title";
				launcherOnCreate = "onCreate";
				launcherGetApplicationContext = "getApplicationContext";
				launcherIsRotationEnabled = "isRotationEnabled";
				celllayoutAddViewToCellLayout = "addViewToCellLayout";
				wallpaperoffsetinterpolatorSyncWithScroll = "syncWithScroll";
				workspaceStartDrag = "startDrag";
				acpvBeginDraggingApplication = "beginDraggingApplication";
				acpvOnPackagesUpdated = "onPackagesUpdated";
				dynamicgridLayout = "layout";
				launcherGetSearchbar = "getSearchBar";
				launcherGetQsbBar = "getQsbBar";
				pagedviewPageBeginMoving = "pageBeginMoving";
				pagedviewPageEndMoving = "pageEndMoving";
				sdtbOnDragStart = "onDragStart";
				sdtbOnDragEnd = "onDragEnd";
				launcherHasCustomContentToLeft = "hasCustomContentToLeft";
				hideAppsCustomizeHelper = "hideAppsCustomizeHelper";
				launcherShowWorkspace = "showWorkspace";
				launcherShowAllApps = "showAllApps";
				workspaceMoveToDefaultScreen = "moveToDefaultScreen";
				btvSetShadowsEnabled = "setShadowsEnabled";	
				wsOverScroll = acpvOverScroll = "overScroll";
				lFinishBindingItems = "finishBindingItems";
				dpGetWorkspacePadding = "getWorkspacePadding";
				lIsAllAppsVisible = "isAllAppsVisible";
				lGetOpenFolder = "getOpenFolder";
				wIsOnOrMovingToCustomContent = "isOnOrMovingToCustomContent";
				wEnterOverviewMode = "enterOverviewMode";
				wMoveToCustomContentScreen = "moveToCustomContentScreen";
				pvSnapToPage = "snapToPage";
				lOpenFolder = "openFolder";
				lCloseFolder = "closeFolder";
				acthOnTabChanged = "onTabChanged";
				wSetCurrentPage = acpvSetCurrentPage = "setCurrentPage";
				aalAdd = "add";
				dpUpdateFromConfiguration = "updateFromConfiguration";
				lHideAppsCustomizeHelper = "hideAppsCustomizeHelper";
				acthSetInsets = "setInsets";
				wSnapToPage = "snapToPage";
				noOnShow = "onShow";
				wOnDragEnd = "onDragEnd";
				wOnDragStart = "onDragStart";
				wOnLauncherTransitionEnd = "onLauncherTransitionEnd";
				fOnRemove = "onRemove";
				fOnAdd = "onAdd";
				fReplaceFolderWithFinalItem = "replaceFolderWithFinalItem";
				fGetItemsInReadingOrder = "getItemsInReadingOrder";
				clGetShortcutsAndWidgets = "getShortcutsAndWidgets";
				soiSetSearchStarted = "setSearchStarted";
				acthGetContentTypeForTabTag = "getContentTypeForTabTag";
				lStartActivitySafely = "startActivitySafely";
				wOnTransitionPrepare = "onTransitionPrepare";
				siGetIntent = "getIntent";
				icGetFullResIcon = "getFullResIcon";
				uCreateIconBitmap = "createIconBitmap";
				icCacheLocked = "cacheLocked";
				clMarkCellsForView = "markCellsForView";
				wStartDrag = "startDrag";
				lmCheckItemPlacement = "checkItemPlacement";
                acpvBeginDragging = "beginDragging";
                lBindAppsUpdated = "bindAppsUpdated";
                lmIsShortcutInfoUpdateable = "isShortcutInfoUpdateable";
                awrfCommitResize = "commitResize";
                clAttemptPushInDirection = "attemptPushInDirection";
                acpvSetApps = "setApps";
                acpvUpdateApps = "updateApps";
                acpvRemoveApps = "removeApps";
			};
		}
	}
}