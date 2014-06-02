package de.theknut.xposedgelsettings.hooks;

import static de.robv.android.xposed.XposedHelpers.findClass;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

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
		oSEARCH_DROP_TARGET_BAR,
		oGEL,
		oNOW_OVERLAY,
		oSEARCH_OVERLAY_IMPL,
		oSEARCH_PLATE;
		
		public static void initNames(String launcherName) {

			String launcherPackage = "com.android." + launcherName + ".";
			ALL_APPS_LIST = launcherPackage + "AllAppsList";
			ITEM_INFO = launcherPackage + "ItemInfo";
			APP_INFO = launcherPackage + "AppInfo";
			CELL_LAYOUT = launcherPackage + "CellLayout";
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
			CELL_INFO = launcherPackage + "CellLayout$CellInfo";
			LOADER_TASK = launcherPackage + "LauncherModel$LoaderTask";

			GEL = "com.google.android.launcher.GEL";
			NOW_OVERLAY = "com.google.android.sidekick.shared.client.NowOverlay";
			SEARCH_OVERLAY_IMPL = "com.google.android.search.gel.SearchOverlayImpl";
			//SEARCH_PLATE = "com.google.android.search.shared.ui.SearchPlate";

			oALL_APPS_LIST = "kp";
			oITEM_INFO = "pr";
			oAPP_INFO = "kr";
			oCELL_LAYOUT = launcherPackage + "CellLayout";
			oSEARCH_DROP_TARGET_BAR = launcherPackage + "SearchDropTargetBar";
			oDEVICE_PROFILE = "mz";
			oLAUNCHER = launcherPackage + "Launcher";
			oPAGED_VIEW = launcherPackage + "PagedView";
			oPAGED_VIEW_CELL_LAYOUT = "vd";
			oPAGED_VIEW_WITH_DRAGGABLE_ITEMS = "vl";
			oAPPS_CUSTOMIZE_PAGED_VIEW = launcherPackage + "AppsCustomizePagedView";
			oAPPS_CUSTOMIZE_CELL_LAYOUT = "kw";
			oPAGED_VIEW_ICON = launcherPackage + "PagedViewIcon";
			oWORKSPACE = launcherPackage + "Workspace";
			oWALLPAPEROFFSETINTERPOLATOR = "zd";
			oAPPS_CUSTOMIZE_TAB_HOST = launcherPackage + "AppsCustomizeTabHost";
			oAPPS_CUSTOMIZE_CONTENT_TYPE = "lf";
			oWALLPAPER_CROP_ACTIVITY = launcherPackage + "WallpaperCropActivity";
			oFOLDER = launcherPackage + "Folder";
			oFOLDER_ICON = launcherPackage + "FolderIcon";
			oCELL_LAYOUT_LAYOUT_PARAMS = launcherPackage + "CellLayout$LayoutParams";
			oCELL_LAYOUT_CELL_INFO = "lz";
			oWORKSPACE_STATE = "zc";
			oHOTSEAT = launcherPackage + "Hotseat";
			oDRAG_SOURCE = "nn";
			oSHORTCUT_INFO = "vz";
			oICON_CACHE = "pk";
			oUTILITIES = "wi";
			oCASH_ENTRY = "pl";
			oCELL_INFO = "nv";
			oLOADER_TASK = "tb";

			oGEL = "com.google.android.launcher.GEL";
			oNOW_OVERLAY = "dzk";
			oSEARCH_OVERLAY_IMPL = "ccu";
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
		LoaderTask;

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
				CellInfo = findClass(ClassNames.CELL_INFO, lpparam.classLoader);
				ItemInfo = findClass(ClassNames.ITEM_INFO, lpparam.classLoader);
				LoaderTask = findClass(ClassNames.LOADER_TASK, lpparam.classLoader);

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
						CellInfo = findClass(ClassNames.oCELL_INFO, lpparam.classLoader);
						ItemInfo = findClass(ClassNames.oITEM_INFO, lpparam.classLoader);
						LoaderTask = findClass(ClassNames.oLOADER_TASK, lpparam.classLoader);
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
		icIcon,
		icIconDensity,
		ciCell,
		lAppWidgetHostView,
		wDragInfo,
		LauncherAppWidgetInfo;

		public static void initFieldNames() {

			if (Common.PACKAGE_OBFUSCATED) {
				hotseatAllAppsRank = "zp";
				dpNumHotseatIcons = "yz";
				itemInfoTitle = "title";
				celllayoutlayoutparamsCanReorder = "wf";
				deviceProfileSearchBarSpaceWidthPx = "zs";
				launcherSearchDropTargetBar = "um";
				launcherQsbBar = "EU";
				sdtbIsSearchBarHidden = "MV";
				sdtbQsbBar = "MW";
				workspaceCustomContentShowing = "PV";
				workspaceCurrentPage = acpvCurrentPage = "KF";
				launcherHotseat = "EO";
				launcherAppsCustomizeTabHost = "ER";
				acthInTransition = "tf";
				workspaceState = "Qj";
				workspaceDefaultPage = "PI";
				bubbleTextView = "BubbleTextView";
				folderIcon = "FolderIcon";
				pagedViewIcon = "PagedViewIcon";
				btvShadowsEnabled = "ue";
				fiPreviewBackground = "CE";
				fiFolderName = "CF";
				fiFolderEditText = "Cf";
				fiFolder = "CB";
				fiFolderIcon = "FolderIcon";
				fFolderIcon = "BL";
				acpvContentType = "sw";
				pvIsPageMoving = "Lv";
				pvNextPage = "KI";
				dpHotseatBarHeightPx = "zo";
				lState = "Et";
				cellInfoClass = "lz";
				shortcutInfoClass = "vz";
				folderInfoClass = "oz";
				wTouchState = "KY";
				lHasFocus = "Fj";
				lPaused = "EZ";
				iiItemType = "En";
				dpSearchBarHeightPx = "zv";
				dpSearchBarSpaceHeightPx = "zu";
				dpSearchBarSpaceWidthPx = "zs";
				aiComponentName = "rJ";
				acpvAllAppsNumCols = "zr";
				acpvAllAppsNumRows = "zq";
				pvPageIndicator = "Lz";
				acthContent = "tD";
				dpPageIndicatorHeightPx = "zw";
				dpDesiredWorkspaceLeftRightMarginPx = "yJ";
				wIsSwitchingState = "Qk";
				fContent = "BH";
				lAppsCustomizePagedView = "ES";
				iiID = "id";
				iiTitle = "title";
				uSCanvas = "NL";
				icIcon = "DZ";
				icIconDensity = "DY";
				ciCell = "vL";
				lAppWidgetHostView = "ru";
				wDragInfo = "PP";
				LauncherAppWidgetInfo = "rv";
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
				icIcon = "icon";
				icIconDensity = "mIconDpi";
				ciCell = "cell";
				lAppWidgetHostView = "LauncherAppWidgetHostView";
				wDragInfo = "mDragInfo";
				LauncherAppWidgetInfo = "LauncherAppWidgetInfo";
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
		lmCheckItemPlacement;

		public static void initMethodNames() {

			if (Common.PACKAGE_OBFUSCATED) {
				applyFromApplicationInfo = "a";
				itemInfoTitle = "title";
				launcherOnCreate = "onCreate";
				launcherGetApplicationContext = "getApplicationContext";
				launcherIsRotationEnabled = "gC";
				celllayoutAddViewToCellLayout = "a";
				wallpaperoffsetinterpolatorSyncWithScroll = "kf";
				workspaceStartDrag = "a";
				workspaceBeginDraggingApplication = "P";
				workspaceBeginDragShared = "a";
				acpvOnPackagesUpdated = "a";
				dynamicgridLayout = "a";
				launcherGetSearchbar = "fZ";
				launcherGetQsbBar = "gw";
				pagedviewPageBeginMoving = "ii";
				pagedviewPageEndMoving = "ij";
				sdtbOnDragStart = "a";
				sdtbOnDragEnd = "dt";
				launcherHasCustomContentToLeft = "fL";
				hideAppsCustomizeHelper = "a";
				launcherShowWorkspace = "a";
				launcherShowAllApps = "a";
				workspaceMoveToDefaultScreen = "ao";
				btvSetShadowsEnabled = "w";
				wsOverScroll = acpvOverScroll = "g";
				lFinishBindingItems = "U";
				dpGetWorkspacePadding = "aC";
				lIsAllAppsVisible = "gs";
				lGetOpenFolder = "jp";
				wIsOnOrMovingToCustomContent = "jJ";
				wEnterOverviewMode = "jO";
				wMoveToCustomContentScreen = "ap";
				pvSnapToPage = "a";
				lOpenFolder = "i";
				lCloseFolder = "gr";
				acthOnTabChanged = "c";
				wSetCurrentPage = acpvSetCurrentPage = "aV";
				aalAdd = "a";
				dpUpdateFromConfiguration = "a";
				lHideAppsCustomizeHelper = "a";
				acthSetInsets = "c";
				wSnapToPage = "bc";
				soiSetSearchStarted = "cs";
				noOnShow = "p";
				wOnDragStart = "a";
				wOnDragEnd = "dt";
				wOnLauncherTransitionEnd = "a"; 
				fOnRemove = "g";
				fOnAdd = "f";
				fReplaceFolderWithFinalItem = "fo";
				fGetItemsInReadingOrder = "fr";
				clGetShortcutsAndWidgets = "dH";
				acthGetContentTypeForTabTag = "j";
				lStartActivitySafely = "b";
				wOnTransitionPrepare = "jR";
				siGetIntent = "getIntent";
				icGetFullResIcon = "a";
				uCreateIconBitmap = "a";
				icCacheLocked = "b";
				clMarkCellsForView = "a";
				wStartDrag = "b";
				lmCheckItemPlacement = "a";
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
			};
		}
	}
}