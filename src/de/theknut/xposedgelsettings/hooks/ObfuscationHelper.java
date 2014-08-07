package de.theknut.xposedgelsettings.hooks;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.findClass;

public class ObfuscationHelper extends HooksBaseClass {

    public static final int GNL_3_3_11 = 300303110;
    public static final int GNL_3_4_15 = 300304150;
    public static final int GNL_3_5_14 = 300305140;
    public static final int GNL_3_6_13 = 300306130;

    public static int getVersionIndex(int version) {

        if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {
            if (version >= GNL_3_6_13) {
                return 3;
            } else if (version >= GNL_3_5_14) {
                return 2;
            } else if (version >= GNL_3_4_15) {
                return 1;
            } else if (version <= GNL_3_3_11) {
                return 0;
            }
        }

        return 0;
    }

    public static void init(LoadPackageParam lpparam, int versionIdx) {
        long time = System.currentTimeMillis();

        ClassNames.initNames(versionIdx);
        Classes.hookAllClasses(lpparam);
        Methods.initMethodNames(versionIdx);
        Fields.initFieldNames(versionIdx);

        if (DEBUG) log("Initialized ObfuscationHelper in " + (System.currentTimeMillis() - time) + "ms");
    }

    public static class ClassNames {

        public static String LAUNCHER,
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
                WALLPAPER_OFFSET_INTERPOLATOR,
                FOLDER,
                FOLDER_ICON,
                HOTSEAT,
                START_SETTINGS_ONCLICK,
                DRAG_SOURCE,
                ITEM_INFO,
                APP_INFO,
                SHORTCUT_INFO,
                SEARCH_DROP_TARGET_BAR,
                ICON_CACHE,
                UTILITIES,
                CACHE_ENTRY,
                LOADER_TASK,
                FOLDER_INFO,
                LAUNCHER_MODEL,
                APP_WIDGET_RESIZE_FRAME,
                ITEM_CONFIGURATION,
                LAUNCHER_APPWIDGET_INFO,
                DRAG_LAYER,
                LAUNCHER_APP_WIDGET_HOST_VIEW,
                BUBBLE_TEXT_VIEW,
                USER_HANDLE,
                ADB,
                GEL,
                NOW_OVERLAY,
                SEARCH_OVERLAY_IMPL,
                GSA_CONFIG_FLAGS,
                RECOGNIZER_VIEW,
                SEARCH_PLATE,
                GEL_SEARCH_PLATE_CONTAINER,
                TRANSITIONS_MANAGER,
                LAUNCHER_APP_STATE;

        public static void initNames(int idx) {

            String launcherPackage = "com.android.launcher3.";
            if (Common.HOOKED_PACKAGE.equals("com.android.launcher2")) {
                launcherPackage = "com.android.launcher2.";
            }

            String[] _LAUNCHER = {launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher"},
                    _WORKSPACE = {launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace"},
                    _WORKSPACE_STATE = {_WORKSPACE[0] + "$State", "zc", "aco", "adq"},
                    _DEVICE_PROFILE = {launcherPackage + "DeviceProfile", "mz", "qi", "rj"}, // All Device Profiles must have
                    _CELL_LAYOUT = {launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout"},
                    _CELL_LAYOUT_CELL_INFO = {_CELL_LAYOUT[0] + "$CellInfo", "lz", "pi", "qj"}, // Cell[=view
                    _CELL_LAYOUT_LAYOUT_PARAMS = {_CELL_LAYOUT[0] + "$LayoutParams", _CELL_LAYOUT[0] + "$LayoutParams", _CELL_LAYOUT[0] + "$LayoutParams", _CELL_LAYOUT[0] + "$LayoutParams"},
                    _PAGED_VIEW = {launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView"},
                    _PAGED_VIEW_ICON = {launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon"},
                    _PAGED_VIEW_CELL_LAYOUT = {launcherPackage + "PagedViewCellLayout", "vd", "yo", "zq"}, // CellLayout cannot have UNSPECIFIED dimensions" the one with more members
                    _PAGED_VIEW_WITH_DRAGGABLE_ITEMS = {launcherPackage + "PagedViewWithDraggableItems", "vl", "yw", "zy"}, // AppsCustomizePagedView extends
                    _APPS_CUSTOMIZE_CELL_LAYOUT = {launcherPackage + "AppsCustomizeCellLayout", "kw", "yr", "zt"}, // "Invalid ContentType" in AppsCostumize - getChildCount
                    _APPS_CUSTOMIZE_LAYOUT = {launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout"}, // Trebuchet only
                    _APPS_CUSTOMIZE_PAGED_VIEW = {launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView"},
                    _APPS_CUSTOMIZE_TAB_HOST = {launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost"},
                    _APPS_CUSTOMIZE_CONTENT_TYPE = {_APPS_CUSTOMIZE_PAGED_VIEW[0] + "$ContentType", "lf", "oo", "pp"},
                    _WALLPAPEROFFSETINTERPOLATOR = {_WORKSPACE[0] + "$WallpaperOffsetInterpolator", "zd", "acp", "adr"}, // Error updating wallpaper offset
                    _FOLDER = {launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder"},
                    _FOLDER_ICON = {launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon"},
                    _HOTSEAT = {launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat"},
                    _START_SETTINGS_ONCLICK = { "", "pu", "td", "ue"}, // in onCreate first setOnClickListener after in if-clause
                    _DRAG_SOURCE = {launcherPackage + "DragSource", "nn", "qw", "rx"}, // first parameter in onDragStart of SearchDropTargetBar
                    _ITEM_INFO = {launcherPackage + "ItemInfo", "pr", "ta", "ub"}, // Item(id=
                    _APP_INFO = {launcherPackage + "AppInfo", "kr", "ob", "pc"}, // firstInstallTime=
                    _SHORTCUT_INFO = {launcherPackage + "ShortcutInfo", "vz", "zl", "aan"}, // ShortcutInfo(title=
                    _SEARCH_DROP_TARGET_BAR = {launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar"},
                    _ICON_CACHE = {launcherPackage + "IconCache", "pk", "ss", "tt"}, // using preloaded icon for
                    _UTILITIES = {launcherPackage + "Utilities", "wi", "zu", "aaw"}, // Launcher.Utilities
                    _CACHE_ENTRY = {_ICON_CACHE[0] + "$CacheEntry", "pl", "st", "tu"}, // new HashMap(50)
                    _LAUNCHER_MODEL = {launcherPackage + "LauncherModel", "sg", "vq", "ws"}, // Error: ItemInfo passed to checkItemInfo doesn't match original
                    _LOADER_TASK = {_LAUNCHER_MODEL[0] + "$LoaderTask", "tb", "wl", "xn"}, // Should not call runBindSynchronousPage
                    _FOLDER_INFO = {launcherPackage + "FolderInfo", "oz", "sh", "ti"}, // FolderInfo(id=
                    _LAUNCHER_APP_STATE = {launcherPackage + "LauncherAppState", "rr", "vb", "wd"}, // Folder onMeasure
                    _APP_WIDGET_RESIZE_FRAME = {launcherPackage + "AppWidgetResizeFrame", "ks", "oc", "pd"}, // in AppsCustomizePagedView search for Bundle its below if (....17)
                    _ITEM_CONFIGURATION = {_CELL_LAYOUT[0] + "$ItemConfiguration", "ma", "pj", "qk"}, // in CellLayout Math.abs(paramArrayOfInt[0])
                    _LAUNCHER_APPWIDGET_INFO = {launcherPackage + "LauncherAppWidgetInfo", "rv", "vf", "wh"}, // AppWidget(id=
                    _DRAG_LAYER = {launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer"},
                    _LAUNCHER_APP_WIDGET_HOST_VIEW = {launcherPackage + "LauncherAppWidgetHostView", "ru", "ve", "wg"}, // in Workspace "getAppWidgetInfo"
                    _BUBBLE_TEXT_VIEW = {launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView"},
                    _USER_HANDLE = {"", "", "adl", "aen"}, // last parameter in IconCache "cacheLocked"
                    _ADB = {"", "", "adb", "aed"},
                    _GEL = {"com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL"},
                    _NOW_OVERLAY = {"com.google.android.sidekick.shared.client.NowOverlay", "dzk", "enc", "evx"}, // now_overlay:views_hidden_for_search
                    _SEARCH_OVERLAY_IMPL = {"com.google.android.search.gel.SearchOverlayImpl", "ccu", "cmh", "cuc"}, // hammerhead
                    _GSA_CONFIG_FLAGS = {"com.google.android.search.core.GsaConfigFlags", "ayc", "bgr", "bnj"}, // Unknown string array encoding
                    _RECOGNIZER_VIEW = {"com.google.android.search.shared.ui.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView"},
                    _SEARCH_PLATE = {"com.google.android.search.shared.ui.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate"},
                    _TRANSITIONS_MANAGER = {"com.google.android.search.shared.ui.SearchPlate$TransitionsManager", "cen", "cog", "cwb"}, // onLayout - SearchPlate
                    _GEL_SEARCH_PLATE_CONTAINER = {"com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer"};


            LAUNCHER = _LAUNCHER[idx];
            WORKSPACE = _WORKSPACE[idx];
            WORKSPACE_STATE = _WORKSPACE_STATE[idx];
            DEVICE_PROFILE = _DEVICE_PROFILE[idx];
            CELL_LAYOUT = _CELL_LAYOUT[idx];
            CELL_LAYOUT_CELL_INFO = _CELL_LAYOUT_CELL_INFO[idx];
            CELL_LAYOUT_LAYOUT_PARAMS = _CELL_LAYOUT_LAYOUT_PARAMS[idx];
            PAGED_VIEW = _PAGED_VIEW[idx];
            PAGED_VIEW_ICON = _PAGED_VIEW_ICON[idx];
            PAGED_VIEW_CELL_LAYOUT = _PAGED_VIEW_CELL_LAYOUT[idx];
            PAGED_VIEW_WITH_DRAGGABLE_ITEMS = _PAGED_VIEW_WITH_DRAGGABLE_ITEMS[idx];
            APPS_CUSTOMIZE_CELL_LAYOUT = _APPS_CUSTOMIZE_CELL_LAYOUT[idx];
            APPS_CUSTOMIZE_LAYOUT = _APPS_CUSTOMIZE_LAYOUT[idx];
            APPS_CUSTOMIZE_PAGED_VIEW = _APPS_CUSTOMIZE_PAGED_VIEW[idx];
            APPS_CUSTOMIZE_TAB_HOST = _APPS_CUSTOMIZE_TAB_HOST[idx];
            APPS_CUSTOMIZE_CONTENT_TYPE = _APPS_CUSTOMIZE_CONTENT_TYPE[idx];
            WALLPAPER_OFFSET_INTERPOLATOR = _WALLPAPEROFFSETINTERPOLATOR[idx];
            FOLDER = _FOLDER[idx];
            FOLDER_ICON = _FOLDER_ICON[idx];
            HOTSEAT = _HOTSEAT[idx];
            START_SETTINGS_ONCLICK = _START_SETTINGS_ONCLICK[idx];
            DRAG_SOURCE = _DRAG_SOURCE[idx];
            ITEM_INFO = _ITEM_INFO[idx];
            APP_INFO = _APP_INFO[idx];
            SHORTCUT_INFO = _SHORTCUT_INFO[idx];
            SEARCH_DROP_TARGET_BAR = _SEARCH_DROP_TARGET_BAR[idx];
            ICON_CACHE = _ICON_CACHE[idx];
            UTILITIES = _UTILITIES[idx];
            CACHE_ENTRY = _CACHE_ENTRY[idx];
            LAUNCHER_MODEL = _LAUNCHER_MODEL[idx];
            LOADER_TASK = _LOADER_TASK[idx];
            FOLDER_INFO = _FOLDER_INFO[idx];
            APP_WIDGET_RESIZE_FRAME = _APP_WIDGET_RESIZE_FRAME[idx];
            ITEM_CONFIGURATION = _ITEM_CONFIGURATION[idx];
            LAUNCHER_APPWIDGET_INFO = _LAUNCHER_APPWIDGET_INFO[idx];
            DRAG_LAYER = _DRAG_LAYER[idx];
            LAUNCHER_APP_WIDGET_HOST_VIEW = _LAUNCHER_APP_WIDGET_HOST_VIEW[idx];
            BUBBLE_TEXT_VIEW = _BUBBLE_TEXT_VIEW[idx];
            USER_HANDLE = _USER_HANDLE[idx];
            ADB = _ADB[idx];
            GEL = _GEL[idx];
            NOW_OVERLAY = _NOW_OVERLAY[idx];
            SEARCH_OVERLAY_IMPL = _SEARCH_OVERLAY_IMPL[idx];
            GSA_CONFIG_FLAGS = _GSA_CONFIG_FLAGS[idx];
            RECOGNIZER_VIEW = _RECOGNIZER_VIEW[idx];
            SEARCH_PLATE = _SEARCH_PLATE[idx];
            TRANSITIONS_MANAGER = _TRANSITIONS_MANAGER[idx];
            LAUNCHER_APP_STATE = _LAUNCHER_APP_STATE[idx];
            GEL_SEARCH_PLATE_CONTAINER = _GEL_SEARCH_PLATE_CONTAINER[idx];
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
                Folder,
                FolderIcon,
                PagedViewWithDraggableItems,
                PagedView,
                GELClass,
                NowOverlay,
                SearchOverlayImpl,
                SearchDropTargetBar,
                DragSource,
                CellLayoutLayoutParams,
                CellLayoutCellInfo,
                WorkspaceState,
                Hotseat,
                StartSettingsOnClick,
                AppsCustomizeContentType,
                ShortcutInfo,
                IconCache,
                Utilities,
                CacheEntry,
                ItemInfo,
                LoaderTask,
                FolderInfo,
                LauncherModel,
                AppWidgetResizeFrame,
                ItemConfiguration,
                UserHandle,
                Adb,
                LauncherAppWidgetInfo,
                DragLayer,
                LauncherAppWidgetHostView,
                GSAConfigFlags,
                RecognizerView,
                SearchPlate,
                GelSearchPlateContainer,
                TransitionsManager,
                BubbleTextView,
                LauncherAppState;

        public static void hookAllClasses(LoadPackageParam lpparam) {
            Launcher = findClass(ClassNames.LAUNCHER, lpparam.classLoader);
            Workspace = findClass(ClassNames.WORKSPACE, lpparam.classLoader);
            AppsCustomizePagedView = findClass(ClassNames.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
            CellLayout = findClass(ClassNames.CELL_LAYOUT, lpparam.classLoader);
            CellLayoutLayoutParams = findClass(ClassNames.CELL_LAYOUT_LAYOUT_PARAMS, lpparam.classLoader);
            WallpaperOffsetInterpolator = findClass(ClassNames.WALLPAPER_OFFSET_INTERPOLATOR, lpparam.classLoader);
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
            CacheEntry = findClass(ClassNames.CACHE_ENTRY, lpparam.classLoader);
            ItemInfo = findClass(ClassNames.ITEM_INFO, lpparam.classLoader);
            LoaderTask = findClass(ClassNames.LOADER_TASK, lpparam.classLoader);
            FolderInfo = findClass(ClassNames.FOLDER_INFO, lpparam.classLoader);
            LauncherModel = findClass(ClassNames.LAUNCHER_MODEL, lpparam.classLoader);
            AppWidgetResizeFrame = findClass(ClassNames.APP_WIDGET_RESIZE_FRAME, lpparam.classLoader);
            ItemConfiguration = findClass(ClassNames.ITEM_CONFIGURATION, lpparam.classLoader);
            LauncherAppWidgetInfo = findClass(ClassNames.LAUNCHER_APPWIDGET_INFO, lpparam.classLoader);
            DragLayer = findClass(ClassNames.DRAG_LAYER, lpparam.classLoader);
            LauncherAppWidgetHostView = findClass(ClassNames.LAUNCHER_APP_WIDGET_HOST_VIEW, lpparam.classLoader);
            BubbleTextView = findClass(ClassNames.BUBBLE_TEXT_VIEW, lpparam.classLoader);
            LauncherAppState = findClass(ClassNames.LAUNCHER_APP_STATE, lpparam.classLoader);

            if (lpparam.packageName.equals(Common.GEL_PACKAGE)) {
                GELClass = findClass(ClassNames.GEL, lpparam.classLoader);
                NowOverlay = findClass(ClassNames.NOW_OVERLAY, lpparam.classLoader);
                SearchOverlayImpl = findClass(ClassNames.SEARCH_OVERLAY_IMPL, lpparam.classLoader);
                GSAConfigFlags = findClass(ClassNames.GSA_CONFIG_FLAGS, lpparam.classLoader);
                TransitionsManager = findClass(ClassNames.TRANSITIONS_MANAGER, lpparam.classLoader);
                RecognizerView = findClass(ClassNames.RECOGNIZER_VIEW, lpparam.classLoader);
                SearchPlate = findClass(ClassNames.SEARCH_PLATE, lpparam.classLoader);
                GelSearchPlateContainer = findClass(ClassNames.GEL_SEARCH_PLATE_CONTAINER, lpparam.classLoader);

                if (Common.PACKAGE_OBFUSCATED) {
                    WorkspaceState = findClass(ClassNames.WORKSPACE_STATE, lpparam.classLoader);

                    if (Common.GNL_VERSION >= ObfuscationHelper.GNL_3_5_14) {
                        UserHandle = findClass(ClassNames.USER_HANDLE, lpparam.classLoader);
                        Adb = findClass(ClassNames.ADB, lpparam.classLoader);
                    }

                    if (Common.GNL_VERSION >= ObfuscationHelper.GNL_3_4_15) {
                        StartSettingsOnClick = findClass(ClassNames.START_SETTINGS_ONCLICK, lpparam.classLoader);
                    }
                }
            }
        }
    }

    public static class Methods {

        public static String applyFromApplicationInfo,
                lGetApplicationContext,
                lIsRotationEnabled,
                clAddViewToCellLayout,
                woiSyncWithScroll,
                wStartDrag,
                acpvOnPackagesUpdated,
                lGetSearchbar,
                lGetQsbBar,
                pvPageBeginMoving,
                pvPageEndMoving,
                sdtbOnDragStart,
                sdtbOnDragEnd,
                lHasCustomContentToLeft,
                hideAppsCustomizeHelper,
                launcherShowWorkspace,
                launcherShowAllApps,
                workspaceMoveToDefaultScreen,
                btvSetShadowsEnabled,
                wsOverScroll,
                lFinishBindingItems,
                dpGetWorkspacePadding,
                lIsAllAppsVisible,
                wGetOpenFolder,
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
                dpUpdateFromConfiguration,
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
                wOnTransitionPrepare,
                siGetIntent,
                icGetFullResIcon,
                uCreateIconBitmap,
                icCacheLocked,
                clMarkCellsForView,
                lmCheckItemPlacement,
                acpvBeginDragging,
                lBindAppsUpdated,
                lmIsShortcutInfoUpdateable,
                clAttemptPushInDirection,
                acpvSetApps,
                acpvUpdateApps,
                acpvRemoveApps,
                lSetWorkspaceBackground,
                lGetDragLayer,
                dlAddResizeFrame,
                gsaShouldAlwaysShowHotwordHint,
                btvCreateGlowingOutline,
                lmDeleteItemFromDatabase,
                lmDeleteFolderContentsFromDatabase,
                siGetIcon,
                rvCanShowHotwordAnimation,
                spSetProximityToNow,
                tmSetTransitionsEnabled,
                uIsL,
                lasIsDisableAllApps;

        public static void initMethodNames(int idx) {

            String[] _applyFromApplicationInfo = {"applyFromApplicationInfo", "a", "a", "a"},
                    _launcherGetApplicationContext = {"getApplicationContext", "getApplicationContext", "getApplicationContext", "getApplicationContext"},
                    _launcherIsRotationEnabled = {"isRotationEnabled", "gC", "hr", "hA"}, // getBoolean - single line method
                    _celllayoutAddViewToCellLayout = {"addViewToCellLayout", "a", "a", "a"}, // View paramView, int paramInt1, int paramInt2, CellLayout.LayoutParams paramLayoutParams, boolean paramBoolean
                    _wallpaperoffsetinterpolatorSyncWithScroll = {"syncWithScroll", "kf", "la", "lf"}, // computeScroll in Workspace
                    _workspaceStartDrag = {"startDrag", "a", "a", "a"}, // isInTouchMode
                    _acpvOnPackagesUpdated = {"onPackagesUpdated", "a", "a", "a"}, // "can not fit on this device"
                    _launcherGetSearchbar = {"getSearchBar", "fZ", "gO", "gX"}, // return SearchDropTargetBar in Launcher
                    _launcherGetQsbBar = {"getQsbBar", "gw", "hl", "hu"}, // public View
                    _pagedviewPageBeginMoving = {"pageBeginMoving", "ii", "iY", "jb"}, // above "awakenScrollBars"
                    _pagedviewPageEndMoving = {"pageEndMoving", "ij", "iZ", "jc"}, // method above "accessibility"
                    _sdtbOnDragStart = {"onDragStart", "a", "a", "a"}, // twice .start in the method
                    _sdtbOnDragEnd = {"onDragEnd", "dt", "ei", "er"}, // twice .reverse
                    _launcherHasCustomContentToLeft = {"hasCustomContentToLeft", "fL", "gA", "gJ"}, // "()) || (!" under isEmpty
                    _hideAppsCustomizeHelper = {"hideAppsCustomizeHelper", "a", "a", "a"},
                    _launcherShowWorkspace = {"showWorkspace", "a", "a", "a"}, // boolean paramBoolean, Runnable paramRunnable
                    _launcherShowAllApps = {"showAllApps", "a", "a", "a"},
                    _workspaceMoveToDefaultScreen = {"moveToDefaultScreen", "ao", "at", "at"}, // Launcher onNewIntent method call of workspace member with (true)
                    _btvSetShadowsEnabled = {"setShadowsEnabled", "w", "z", "z"}, // invalidate
                    _wsOverScroll = {"overScroll", "g", "g", "g"}, // (float paramFloat)
                    _acpvOverScroll = {"overScroll", "g", "g", "g"}, // (float paramFloat)
                    _lFinishBindingItems = {"finishBindingItems", "U", "Z", "Z"}, // hasFocus()
                    _dpGetWorkspacePadding = {"getWorkspacePadding", "aC", "aS", "aS"}, // second method with (int paramInt)
                    _lIsAllAppsVisible = {"isAllAppsVisible", "gs", "hh", "hq"}, // onBackPressed first method call
                    _wGetOpenFolder = {"getOpenFolder", "jp", "kj", "kn"}, // localDragLayer.getChildCount();
                    _wIsOnOrMovingToCustomContent = {"isOnOrMovingToCustomContent", "jJ", "kE", "kI"}, // last if-clause in Launcher onResume
                    _wEnterOverviewMode = {"enterOverviewMode", "jO", "kJ", "kN"}, // "()) || (!this."
                    _wMoveToCustomContentScreen = {"moveToCustomContentScreen", "ap", "au", "au"}, // Workspace "View localView = getChildAt"
                    _pvSnapToPage = {"snapToPage", "a", "a", "a"}, // int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, TimeInterpolator paramTimeInterpolator
                    _lOpenFolder = {"openFolder", "i", "i", "i"}, // "Opening folder ("
                    _lCloseFolder = {"closeFolder", "gr", "hg", "hq"}, // localFolder != null
                    _acthOnTabChanged = {"onTabChanged", "c", "c", "c"}, // setBackgroundColor
                    _wSetCurrentPage = {"setCurrentPage", "aV", "bl", "bm"},
                    _acpvSetCurrentPage = {"setCurrentPage", "aV", "bl", "bm"},
                    _dpUpdateFromConfiguration = {"updateFromConfiguration", "a", "a", "a"}, // float paramFloat, int paramInt, Resources paramResources, DisplayMetrics paramDisplayMetrics
                    _acthSetInsets = {"setInsets", "c", "c", "c"}, // (Rect
                    _wSnapToPage = {"snapToPage", "bc", "bs", "bt"}, // in PagedView requestChildFocus
                    _soiSetSearchStarted = {"setSearchStarted", "cs", "cI", "cI"}, // onResume before cancel()
                    _noOnShow = {"onShow", "p", "u", "v"}, // boolean paramBoolean1, boolean paramBoolean2
                    _wOnDragEnd = {"onDragEnd", "dt", "ei", "er"}, // only method without interface parameters with InstallShortcutReceiver
                    _wOnDragStart = {"onDragStart", "a", "a", "a"}, // only method with interface parameters with InstallShortcutReceiver
                    _wOnLauncherTransitionEnd = {"onLauncherTransitionEnd", "a", "a", "a"}, // (Launcher paramLauncher, boolean paramBoolean1, boolean paramBoolean2)
                    _fOnRemove = {"onRemove", "g", "g", "g"}, // removeView(localView)
                    _fOnAdd = {"onAdd", "f", "f", "f"}, // (1 + getItemCount()); - first line  = true
                    _fReplaceFolderWithFinalItem = {"replaceFolderWithFinalItem", "ge", "ge", "gn"}, // if (localView != null)
                    _fGetItemsInReadingOrder = {"getItemsInReadingOrder", "fr", "gh", "gq"}, // public final ArrayList
                    _clGetShortcutsAndWidgets = {"getShortcutsAndWidgets", "dH", "ew", "eF"}, // getChildCount() > 0
                    _acthGetContentTypeForTabTag = {"getContentTypeForTabTag", "j", "r", "r"}, // (String paramString)
                    _wOnTransitionPrepare = {"onTransitionPrepare", "jR", "kM", "kR"}, // "if ((bool) && ("
                    _siGetIntent = {"getIntent", "getIntent", "getIntent", "getIntent"},
                    _icGetFullResIcon = {"getFullResIcon", "a", "a", "a"}, // (Resources paramResources, int paramInt)
                    _uCreateIconBitmap = {"createIconBitmap", "a", "a", "a"}, // (Drawable paramDrawable, Context paramContext)
                    _icCacheLocked = {"cacheLocked", "b", "a", "a"},
                    _clMarkCellsForView = {"markCellsForView", "a", "a", "a"}, // int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean[][] paramArrayOfBoolean, boolean paramBoolean
                    _lmCheckItemPlacement = {"checkItemPlacement", "a", "a", "a"}, // "Error loading shortcut into "
                    _acpvBeginDragging = {"beginDragging", "n", "n", "n"}, // "instanceof PagedViewIcon" in AppsCustomizePagedView
                    _lBindAppsUpdated = {"bindAppsUpdated", "l", "l", "l"}, // "(this, paramArrayList), false));"
                    _lmIsShortcutInfoUpdateable = {"isShortcutInfoUpdateable", "e", "e", "e"}, // "android.intent.action.MAIN"
                    _clAttemptPushInDirection = {"attemptPushInDirection", "b", "b", "b"}, // "if (Math.abs(paramArrayOfInt[0]) + Math.abs(paramArrayOfInt[1]) > 1)"
                    _acpvSetApps = {"setApps", "b", "b", "b"}, // Collections.sort
                    _acpvUpdateApps = {"updateApps", "g", "g", "g"}, // in BindAppsUpdated in Launcher
                    _acpvRemoveApps = {"removeApps", "f", "f", "f"}, // in Launcher removeApps."(paramArrayList2)"
                    _lSetWorkspaceBackground = {"setWorkspaceBackground", "N", "S", "S"}, // setBackground
                    _lGetDragLayer = {"getDragLayer", "fV", "gK", "gT"}, // public final DragLayer
                    _dlAddResizeFrame = {"addResizeFrame", "a", "a", "a"}, // (-1, -1)
                    _gsaShouldAlwaysShowHotwordHint = {"shouldAlwaysShowHotwordHint", "uK", "xE", "yB"}, // always_show_hotword_hint
                    _btvCreateGlowingOutline = {"createGlowingOutline", "a", "a", "a"}, // setBitmap
                    _lmDeleteItemFromDatabase = {"deleteItemFromDatabase", "b", "b", "b"}, // (Context paramContext, ItemInfo paramta) - link to "deleting a folder"
                    _siGetIcon = {"getIcon", "a", "a", "a"}, // public final Bitmap
                    _lmDeleteFolderContentsFromDatabase = {"deleteFolderContentsFromDatabase", "a", "a", "a"}, // (Context paramContext, FolderInfo paramsh)
                    _rvCanShowHotwordAnimation = {"canShowHotwordAnimation", "NH", "Se", "UC"}, // == 5
                    _spSetProximityToNow = {"setProximityToNow", "x", "x", "x"}, // (float paramFloat) with RecognizerView
                    _tmSetTransitionsEnabled = {"setTransitionsEnabled", "cG", "cY", "cZ"}, // (4)
                    _uIsL = {"", "", "jO", "jS"},
                    _lasIsDisableAllApps = {"isDisableAllApps", "ha", "hS", "hW"};

            applyFromApplicationInfo = _applyFromApplicationInfo[idx];
            lGetApplicationContext = _launcherGetApplicationContext[idx];
            lIsRotationEnabled = _launcherIsRotationEnabled[idx];
            clAddViewToCellLayout = _celllayoutAddViewToCellLayout[idx];
            woiSyncWithScroll = _wallpaperoffsetinterpolatorSyncWithScroll[idx];
            wStartDrag = _workspaceStartDrag[idx];
            acpvOnPackagesUpdated = _acpvOnPackagesUpdated[idx];
            lGetSearchbar = _launcherGetSearchbar[idx];
            lGetQsbBar = _launcherGetQsbBar[idx];
            pvPageBeginMoving = _pagedviewPageBeginMoving[idx];
            pvPageEndMoving = _pagedviewPageEndMoving[idx];
            sdtbOnDragStart = _sdtbOnDragStart[idx];
            sdtbOnDragEnd = _sdtbOnDragEnd[idx];
            lHasCustomContentToLeft = _launcherHasCustomContentToLeft[idx];
            hideAppsCustomizeHelper = _hideAppsCustomizeHelper[idx];
            launcherShowWorkspace = _launcherShowWorkspace[idx];
            launcherShowAllApps = _launcherShowAllApps[idx];
            workspaceMoveToDefaultScreen = _workspaceMoveToDefaultScreen[idx];
            btvSetShadowsEnabled = _btvSetShadowsEnabled[idx];
            wsOverScroll = _wsOverScroll[idx];
            acpvOverScroll = _acpvOverScroll[idx];
            lFinishBindingItems = _lFinishBindingItems[idx];
            dpGetWorkspacePadding = _dpGetWorkspacePadding[idx];
            lIsAllAppsVisible = _lIsAllAppsVisible[idx];
            wGetOpenFolder = _wGetOpenFolder[idx];
            wIsOnOrMovingToCustomContent = _wIsOnOrMovingToCustomContent[idx];
            wEnterOverviewMode = _wEnterOverviewMode[idx];
            wMoveToCustomContentScreen = _wMoveToCustomContentScreen[idx];
            pvSnapToPage = _pvSnapToPage[idx];
            lOpenFolder = _lOpenFolder[idx];
            lCloseFolder = _lCloseFolder[idx];
            acthOnTabChanged = _acthOnTabChanged[idx];
            wSetCurrentPage = _wSetCurrentPage[idx];
            acpvSetCurrentPage = _acpvSetCurrentPage[idx];
            dpUpdateFromConfiguration = _dpUpdateFromConfiguration[idx];
            acthSetInsets = _acthSetInsets[idx];
            wSnapToPage = _wSnapToPage[idx];
            noOnShow = _noOnShow[idx];
            wOnDragEnd = _wOnDragEnd[idx];
            wOnDragStart = _wOnDragStart[idx];
            wOnLauncherTransitionEnd = _wOnLauncherTransitionEnd[idx];
            fOnRemove = _fOnRemove[idx];
            fOnAdd = _fOnAdd[idx];
            fReplaceFolderWithFinalItem = _fReplaceFolderWithFinalItem[idx];
            fGetItemsInReadingOrder = _fGetItemsInReadingOrder[idx];
            clGetShortcutsAndWidgets = _clGetShortcutsAndWidgets[idx];
            soiSetSearchStarted = _soiSetSearchStarted[idx];
            acthGetContentTypeForTabTag = _acthGetContentTypeForTabTag[idx];
            wOnTransitionPrepare = _wOnTransitionPrepare[idx];
            siGetIntent = _siGetIntent[idx];
            icGetFullResIcon = _icGetFullResIcon[idx];
            uCreateIconBitmap = _uCreateIconBitmap[idx];
            icCacheLocked = _icCacheLocked[idx];
            clMarkCellsForView = _clMarkCellsForView[idx];
            lmCheckItemPlacement = _lmCheckItemPlacement[idx];
            acpvBeginDragging = _acpvBeginDragging[idx];
            lBindAppsUpdated = _lBindAppsUpdated[idx];
            lmIsShortcutInfoUpdateable = _lmIsShortcutInfoUpdateable[idx];
            clAttemptPushInDirection = _clAttemptPushInDirection[idx];
            acpvSetApps = _acpvSetApps[idx];
            acpvUpdateApps = _acpvUpdateApps[idx];
            acpvRemoveApps = _acpvRemoveApps[idx];
            lSetWorkspaceBackground = _lSetWorkspaceBackground[idx];
            lGetDragLayer = _lGetDragLayer[idx];
            dlAddResizeFrame = _dlAddResizeFrame[idx];
            gsaShouldAlwaysShowHotwordHint = _gsaShouldAlwaysShowHotwordHint[idx];
            btvCreateGlowingOutline = _btvCreateGlowingOutline[idx];
            lmDeleteItemFromDatabase = _lmDeleteItemFromDatabase[idx];
            lmDeleteFolderContentsFromDatabase = _lmDeleteFolderContentsFromDatabase[idx];
            siGetIcon = _siGetIcon[idx];
            rvCanShowHotwordAnimation = _rvCanShowHotwordAnimation[idx];
            spSetProximityToNow = _spSetProximityToNow[idx];
            tmSetTransitionsEnabled = _tmSetTransitionsEnabled[idx];
            uIsL = _uIsL[idx];
            lasIsDisableAllApps = _lasIsDisableAllApps[idx];
        }
    }

    public static class Fields {

        public static String hotseatAllAppsRank,
                dpNumHotseatIcons,
                cllpCanReorder,
                sdtbIsSearchBarHidden,
                sdtbQsbBar,
                wCustomContentShowing,
                wCurrentPage,
                lHotseat,
                lAppsCustomizeTabHost,
                acthInTransition,
                wState,
                wDefaultPage,
                btvShadowsEnabled,
                fiPreviewBackground,
                fiFolderName,
                fFolderEditText,
                fiFolder,
                fiContents,
                fFolderIcon,
                acpvContentType,
                pvIsPageMoving,
                wIsSwitchingState,
                dpHotseatBarHeightPx,
                lState,
                wTouchState,
                pvNextPage,
                lHasFocus,
                lPaused,
                iiItemType,
                aiComponentName,
                acpvCurrentPage,
                acpvAllAppsNumCols,
                acpvAllAppsNumRows,
                pvPageIndicator,
                acthContent,
                dpPageIndicatorHeightPx,
                fContent,
                lAppsCustomizePagedView,
                iiID,
                ceIcon,
                ceTitle,
                lIconCache,
                iiTitle,
                fiLongPressHelper,
                clphHasPerformedLongPress,
                lawiProviderName,
                fMaxCountY,
                fMaxCountX,
                fMaxNumItems;

        public static void initFieldNames(int idx) {

            String[] _hotseatAllAppsRank = {"hotseatAllAppsRank", "zp", "BQ", "Cv"}, // only / 2 operation
                    _dpNumHotseatIcons = {"numHotseatIcons", "yz", "AY", "BD"}, // toString of DynamicGrid
                    _cllpCanReorder = {"canReorder", "wf", "yE", "zj"}, // second member with = true
                    _sdtbIsSearchBarHidden = {"mIsSearchBarHidden", "MV", "PF", "Qg"}, // above Qsb member
                    _sdtbQsbBar = {"mQSBSearchBar", "MW", "PG", "Qh"},
                    _wCustomContentShowing = {"mCustomContentShowing", "PV", "SH", "Ti"}, // "() == 0) || (!this."
                    _wCurrentPage = {"mCurrentPage", "KF", "Nm", "NQ"}, // in OnTouch -> "indexOfChild(paramView) != this."
                    _acpvCurrentPage = {"mCurrentPage", "KF", "Nm", "NQ"}, // == _wCurrentPage
                    _lHotseat = {"mHotseat", "EO", "Hu", "HZ"},
                    _lAppsCustomizeTabHost = {"mAppsCustomizeTabHost", "ER", "Hx", "Ic"},
                    _acthInTransition = {"mInTransition", "tf", "vF", "wk"}, // onInterceptTouchEvent first member in if-clause
                    _wState = {"mState", "Qj", "SV", "Tw"}, // WorkspaceState member
                    _wDefaultPage = {"mDefaultPage", "PI", "Su", "SV"},  // "Expected custom content", member gets decreased by one // " = (-1 + this."
                    _btvShadowsEnabled = {"mShadowsEnabled", "ue", "wF", "xk"}, // only boolean member = true
                    _fiPreviewBackground = {"mPreviewBackground", "CE", "Fh", "FM"}, // FOLDERICON - only ImageView member
                    _fiFolderName = {"mFolderName", "CF", "Fi", "FN"}, // FOLDERICON - only BubbleTextView
                    _fiFolder = {"mFolder", "CB", "Fe", "FJ"}, // FOLDERICON - only Folder member
                    _fiContents = {"contents", "Dt", "FW", "GB"}, // first ArrayList in FolderInfo
                    _fFolderIcon = {"mFolderIcon", "BL", "Ep", "EU"}, // only FolderIcon member
                    _fFolderEditText = {"mFolderName", "Cf", "EJ", "Fo"}, // only FolderEditText member
                    _acpvContentType = {"mContentType", "sw", "uW", "vB"}, // private oo uW = oo.vW;
                    _pvIsPageMoving = {"mIsPageMoving", "Lv", "Oc", "OG"},  // "while (!this."
                    _dpHotseatBarHeightPx = {"hotseatBarHeightPx", "zo", "BP", "Cu"}, // 4 * ...
                    _lState = {"mState", "Et", "GZ", "HE"}, // onNewIntent - "if ((i != 0) && (this."
                    _wTouchState = {"mTouchState", "KY", "NF", "Oj"}, // onInterceptTouchEvent while clause
                    _pvNextPage = {"mNextPage", "KI", "Np", "NT"}, // first protected int = -1
                    _lHasFocus = {"mHasFocus", "Fj", "HP", "It"}, // onWindowFocusChanged
                    _lPaused = {"mPaused", "EZ", "HF", "Ik"}, // only boolean assignement in onPause()
                    _iiItemType = {"itemType", "En", "GT", "Hy"}, // Item(id=
                    _aiComponentName = {"componentName", "rJ", "uj", "uO"}, // only ComponentName member
                    _acpvAllAppsNumCols = {"allAppsNumCols", "zr", "BS", "Cx"}, // onMeasure localDeviceProfile
                    _acpvAllAppsNumRows = {"allAppsNumRows", "zq", "BR", "Cw"}, // onMeasure localDeviceProfile
                    _pvPageIndicator = {"mPageIndicator", "Lz", "Og", "OK"}, // setContentDescription
                    _acthContent = {"mContent", "tD", "wd", "wI"}, // .getLayoutParams in setInsets
                    _dpPageIndicatorHeightPx = {"pageIndicatorHeightPx", "zw", "BX", "CC"}, // last parameter in .set
                    _wIsSwitchingState = {"mIsSwitchingState", "Qk", "SW", "Tx"}, // start from onTouch, second method call in if-clause
                    _fContent = {"mContent", "BH", "El", "EQ"}, // only CellLayout member
                    _lAppsCustomizePagedView = {"mAppsCustomizeContent", "ES", "Hy", "Id"}, // AppsCustomizePagedView in Launcher
                    _iiID = {"id", "id", "id", "id"},
                    _iiTitle = {"title", "title", "title", "title"},
                    _ceIcon = {"icon", "DZ", "GE", "Hj"},
                    _ceTitle = {"title", "title", "title", "title"},
                    _lIconCache = {"mIconCache", "rF", "uf", "uK"}, // IconCache member in Launcher
                    _fiLongPressHelper = {"mLongPressHelper", "ui", "wJ", "xo"}, // cancelLongPress
                    _clphHasPerformedLongPress = {"mHasPerformedLongPress", "wG", "zf", "zK"}, // only boolean member
                    _lawiProviderName = {"providerName", "GX", "JF", "Kj"}, // only ComponentName member
                    _fMaxCountX = {"mMaxCountX", "BM", "Eq", "EV"}, // Folder constructor, last line - maxNumItems = X * Y;
                    _fMaxCountY = {"mMaxCountY", "BN", "Er", "EW"}, // Folder constructor, last line - maxNumItems = X * Y;
                    _fMaxNumItems = {"mMaxNumItems", "BO", "Es", "EX"}; // Folder constructor, last line - maxNumItems = X * Y;

            hotseatAllAppsRank = _hotseatAllAppsRank[idx];
            dpNumHotseatIcons = _dpNumHotseatIcons[idx];
            cllpCanReorder = _cllpCanReorder[idx];
            sdtbIsSearchBarHidden = _sdtbIsSearchBarHidden[idx];
            sdtbQsbBar = _sdtbQsbBar[idx];
            wCustomContentShowing = _wCustomContentShowing[idx];
            wCurrentPage = _wCurrentPage[idx];
            lHotseat = _lHotseat[idx];
            lAppsCustomizeTabHost = _lAppsCustomizeTabHost[idx];
            acthInTransition = _acthInTransition[idx];
            wState = _wState[idx];
            wDefaultPage = _wDefaultPage[idx];
            btvShadowsEnabled = _btvShadowsEnabled[idx];
            fiPreviewBackground = _fiPreviewBackground[idx];
            fiFolderName = _fiFolderName[idx];
            fiFolder = _fiFolder[idx];
            fiContents = _fiContents[idx];
            fFolderIcon = _fFolderIcon[idx];
            fFolderEditText = _fFolderEditText[idx];
            acpvContentType = _acpvContentType[idx];
            pvIsPageMoving = _pvIsPageMoving[idx];
            dpHotseatBarHeightPx = _dpHotseatBarHeightPx[idx];
            lState = _lState[idx];
            wTouchState = _wTouchState[idx];
            pvNextPage = _pvNextPage[idx];
            lHasFocus = _lHasFocus[idx];
            lPaused = _lPaused[idx];
            iiItemType = _iiItemType[idx];
            aiComponentName = _aiComponentName[idx];
            acpvAllAppsNumCols = _acpvAllAppsNumCols[idx];
            acpvAllAppsNumRows = _acpvAllAppsNumRows[idx];
            pvPageIndicator = _pvPageIndicator[idx];
            acthContent = _acthContent[idx];
            dpPageIndicatorHeightPx = _dpPageIndicatorHeightPx[idx];
            wIsSwitchingState = _wIsSwitchingState[idx];
            fContent = _fContent[idx];
            lAppsCustomizePagedView = _lAppsCustomizePagedView[idx];
            iiID = _iiID[idx];
            iiTitle = _iiTitle[idx];
            ceIcon = _ceIcon[idx];
            ceTitle = _ceTitle[idx];
            lIconCache = _lIconCache[idx];
            fiLongPressHelper = _fiLongPressHelper[idx];
            clphHasPerformedLongPress = _clphHasPerformedLongPress[idx];
            lawiProviderName = _lawiProviderName[idx];
            fMaxCountX = _fMaxCountX[idx];
            fMaxCountY = _fMaxCountY[idx];
            fMaxNumItems = _fMaxNumItems[idx];
            acpvCurrentPage = _acpvCurrentPage[idx];
        }
    }
}