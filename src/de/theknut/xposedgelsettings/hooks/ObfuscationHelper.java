package de.theknut.xposedgelsettings.hooks;

import static de.robv.android.xposed.XposedHelpers.findClass;
import de.robv.android.xposed.XposedHelpers.ClassNotFoundError;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class ObfuscationHelper extends HooksBaseClass {
	
	// class names to hook to	
	public static String ALL_APPS_LIST,
	ITEM_INFO,
	APP_INFO,
	CELL_LAYOUT,
	SEARCH_DROP_TARGET_BAR,
	DYNAMIC_GRID,
	LAUNCHER,
	PAGED_VIEW,
	PAGED_VIEW_CELL_LAYOUT,
	PAGED_VIEW_WITH_DRAGGABLE_ITEMS,
	APPS_CUSTOMIZE_PAGED_VIEW,
	APPS_CUSTOMIZE_LAYOUT,
	APPS_CUSTOMIZE_CELL_LAYOUT,
	PAGED_VIEW_ICON,
	DEVICE_PROFILE,
	WORKSPACE,
	WALLPAPEROFFSETINTERPOLATOR,
	CELL_LAYOUT_LAYOUT_PARAMS,
	APPS_CUSTOMIZE_TAB_HOST,
	APPS_CUSTOMIZE_CONTENT_TYPE,
	WALLPAPER_CROP_ACTIVITY,
	FOLDER,
	FOLDER_ICON,
	HOTSEAT,
	DRAG_SOURCE,
	SHORTCUT_INFO,

	GEL,
	NOW_OVERLAY,
	SEARCH_OVERLAY_IMPL,
	SEARCH_PLATE;
		
	// prefix o stands for obfuscated
	public static String oALL_APPS_LIST,
	oITEM_INFO,
	oAPP_INFO,
	oCELL_LAYOUT,
	oSEARCH_DROP_TARGET_BAR,
	oLAUNCHER,
	oPAGED_VIEW,
	oPAGED_VIEW_CELL_LAYOUT,
	oPAGED_VIEW_WITH_DRAGGABLE_ITEMS,
	oAPPS_CUSTOMIZE_PAGED_VIEW,
	oAPPS_CUSTOMIZE_LAYOUT,
	oAPPS_CUSTOMIZE_CELL_LAYOUT,
	oPAGED_VIEW_ICON,
	oDEVICE_PROFILE,
	oWORKSPACE,
	oWALLPAPEROFFSETINTERPOLATOR,
	oAPPS_CUSTOMIZE_TAB_HOST,
	oAPPS_CUSTOMIZE_CONTENT_TYPE,
	oWALLPAPER_CROP_ACTIVITY,
	oFOLDER,
	oFOLDER_ICON,
	oCELL_LAYOUT_LAYOUT_PARAMS,
	oCELL_LAYOUT_CELL_INFO,
	oWORKSPACE_STATE,
	oHOTSEAT,
	oDRAG_SOURCE,
	oSHORTCUT_INFO,

	oGEL,
	oNOW_OVERLAY,
	oSEARCH_OVERLAY_IMPL,
	oSEARCH_PLATE;
	
	public static void initClassNames(String launcherName) {
		
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
		
		GEL = "com.google.android.launcher.GEL";
		NOW_OVERLAY = "com.google.android.sidekick.shared.client.NowOverlay";
		SEARCH_OVERLAY_IMPL = "com.google.android.search.gel.SearchOverlayImpl";
		SEARCH_PLATE = "com.google.android.search.shared.ui.SearchPlate";
		
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
		
		oGEL = "com.google.android.launcher.GEL";
		oNOW_OVERLAY = "dzk";
		oSEARCH_OVERLAY_IMPL = "ccu";
		oSEARCH_PLATE = "com.google.android.search.searchplate.SearchPlate";
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
		SearchPlate,
		SearchDropTargetBar,
		DragSource,
		CellLayoutLayoutParams,
		CellLayoutCellInfo,
		WorkspaceState,
		Hotseat,
		AppsCustomizeContentType,
		ShortcutInfo;
		
		public static void hookAllClasses(LoadPackageParam lpparam) {
			try {
				Launcher = findClass(LAUNCHER, lpparam.classLoader);
				Workspace = findClass(WORKSPACE, lpparam.classLoader);
				AppsCustomizePagedView = findClass(APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
				CellLayout = findClass(CELL_LAYOUT, lpparam.classLoader);
				CellLayoutLayoutParams = findClass(CELL_LAYOUT_LAYOUT_PARAMS, lpparam.classLoader);
				WallpaperOffsetInterpolator = findClass(WALLPAPEROFFSETINTERPOLATOR, lpparam.classLoader);
				PagedViewIcon = findClass(PAGED_VIEW_ICON, lpparam.classLoader);
				DeviceProfile = findClass(DEVICE_PROFILE, lpparam.classLoader);
				AppInfo = findClass(APP_INFO, lpparam.classLoader);
				
				if (Common.HOOKED_PACKAGE.equals(Common.TREBUCHET_PACKAGE)) {
					AppsCustomizeLayout = findClass(APPS_CUSTOMIZE_LAYOUT, lpparam.classLoader);
				} else {
					AppsCustomizeTabHost = findClass(APPS_CUSTOMIZE_TAB_HOST, lpparam.classLoader);
				}
				
				AppsCustomizeContentType = findClass(APPS_CUSTOMIZE_CONTENT_TYPE, lpparam.classLoader);
				AllAppsList = findClass(ALL_APPS_LIST, lpparam.classLoader);
				Folder = findClass(FOLDER, lpparam.classLoader);
				PagedViewWithDraggableItems = findClass(PAGED_VIEW_WITH_DRAGGABLE_ITEMS, lpparam.classLoader);
				PagedView = findClass(PAGED_VIEW, lpparam.classLoader);
				FolderIcon = findClass(FOLDER_ICON, lpparam.classLoader);
				Hotseat = findClass(HOTSEAT, lpparam.classLoader);
				DragSource = findClass(DRAG_SOURCE, lpparam.classLoader);
				ShortcutInfo = findClass(SHORTCUT_INFO, lpparam.classLoader);
				SearchDropTargetBar = findClass(SEARCH_DROP_TARGET_BAR, lpparam.classLoader);
				
				if (lpparam.packageName.equals(Common.GEL_PACKAGE)) {
					GELClass = findClass(GEL, lpparam.classLoader);
					NowOverlay = findClass(NOW_OVERLAY, lpparam.classLoader);
					SearchOverlayImpl = findClass(SEARCH_OVERLAY_IMPL, lpparam.classLoader);
					SearchPlate = findClass(SEARCH_PLATE, lpparam.classLoader);					
				}
								
				if (PreferencesHelper.Debug) log("Hooking non-obfuscated GNL");
			} catch (ClassNotFoundError cnfe) {
				if (PreferencesHelper.Debug) {
					log("Upsi! " + cnfe);
					log("Couldn't hook classes, trying obfuscated classes now");
				}
				
				try {
					if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE))	{
						Launcher = findClass(oLAUNCHER, lpparam.classLoader);
						Workspace = findClass(oWORKSPACE, lpparam.classLoader);
						AppsCustomizePagedView = findClass(oAPPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
						CellLayout = findClass(oCELL_LAYOUT, lpparam.classLoader);
						WallpaperOffsetInterpolator = findClass(oWALLPAPEROFFSETINTERPOLATOR, lpparam.classLoader);
						PagedViewIcon = findClass(oPAGED_VIEW_ICON, lpparam.classLoader);
						DeviceProfile = findClass(oDEVICE_PROFILE, lpparam.classLoader);
						AppsCustomizeTabHost = findClass(oAPPS_CUSTOMIZE_TAB_HOST, lpparam.classLoader);
						AppsCustomizeContentType = findClass(oAPPS_CUSTOMIZE_CONTENT_TYPE, lpparam.classLoader);
						AllAppsList = findClass(oALL_APPS_LIST, lpparam.classLoader);
						Folder = findClass(oFOLDER, lpparam.classLoader);
						PagedViewWithDraggableItems = findClass(oPAGED_VIEW_WITH_DRAGGABLE_ITEMS, lpparam.classLoader);
						PagedView = findClass(oPAGED_VIEW, lpparam.classLoader);
						FolderIcon = findClass(oFOLDER_ICON, lpparam.classLoader);
						CellLayoutLayoutParams = findClass(oCELL_LAYOUT_LAYOUT_PARAMS, lpparam.classLoader);
						CellLayoutCellInfo = findClass(oCELL_LAYOUT_CELL_INFO, lpparam.classLoader);
						WorkspaceState = findClass(oWORKSPACE_STATE, lpparam.classLoader);
						Hotseat = findClass(oHOTSEAT, lpparam.classLoader);
						AppInfo = findClass(oAPP_INFO, lpparam.classLoader);
						ShortcutInfo = findClass(oSHORTCUT_INFO, lpparam.classLoader);
						DragSource = findClass(oDRAG_SOURCE, lpparam.classLoader);
						SearchDropTargetBar = findClass(oSEARCH_DROP_TARGET_BAR, lpparam.classLoader);
						Common.PACKAGE_OBFUSCATED = true;

						GELClass = findClass(oGEL, lpparam.classLoader);
						NowOverlay = findClass(oNOW_OVERLAY, lpparam.classLoader);
						SearchOverlayImpl = findClass(oSEARCH_OVERLAY_IMPL, lpparam.classLoader);
						SearchPlate = findClass(oSEARCH_PLATE, lpparam.classLoader);
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
		iiID;
		
		public static void initFieldNames() {
			
			if (Common.PACKAGE_OBFUSCATED) {
				hotseatAllAppsRank = "zp";
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
			} else {
				hotseatAllAppsRank = "hotseatAllAppsRank";
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
		launcherAppWidgetHostView,
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
		wOnTransitionPrepare;
		
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
				launcherAppWidgetHostView = "ru";
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
				launcherAppWidgetHostView = "LauncherAppWidgetHostView";
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
			};
		}
	}
}
