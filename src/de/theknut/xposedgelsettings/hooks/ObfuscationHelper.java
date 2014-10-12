package de.theknut.xposedgelsettings.hooks;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class ObfuscationHelper extends HooksBaseClass {

    public static final int GNL_3_3_11 = 300303110;
    public static final int GNL_3_4_15 = 300304150;
    public static final int GNL_3_5_14 = 300305140;
    public static final int GNL_3_6_13 = 300306130;
    public static final int GNL_3_9_00 = 300309000;

    public static int getVersionIndex(int version) {

        if (Common.HOOKED_PACKAGE.equals(Common.GEL_PACKAGE)) {
            if (version >= GNL_3_9_00) {
                return 4;
            } else if (version >= GNL_3_6_13) {
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
        Methods.initMethodNames(versionIdx);
        Fields.initFieldNames(versionIdx);
        Classes.hookAllClasses(lpparam);

        if (DEBUG) log("Initialized ObfuscationHelper in " + (System.currentTimeMillis() - time) + "ms");
    }

    public static class ClassNames {

        public static String LAUNCHER,
                WORKSPACE,
                WORKSPACE_STATE,
                DEVICE_PROFILE,
                DYNAMIC_GRID,
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

            LAUNCHER = new String[]{launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher"}[idx];
            WORKSPACE = new String[]{launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace"}[idx];
            WORKSPACE_STATE = new String[]{WORKSPACE + "$State", "zc", "aco", "adq", "adr"}[idx]; // member initialization from Enum
            DEVICE_PROFILE = new String[]{launcherPackage + "DeviceProfile", "mz", "qi", "rj", "rj"}[idx]; // All Device Profiles must have
            DYNAMIC_GRID = new String[]{launcherPackage + "DynamicGrid", "nw", "rf", "sg", "sg"}[idx]; // All Device Profiles must have
            CELL_LAYOUT = new String[]{launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout"}[idx];
            CELL_LAYOUT_CELL_INFO = new String[]{CELL_LAYOUT + "$CellInfo", "lz", "pi", "qj", "qj"}[idx]; // Cell[view=
            CELL_LAYOUT_LAYOUT_PARAMS = new String[]{CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams"}[idx];
            PAGED_VIEW = new String[]{launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView"}[idx];
            PAGED_VIEW_ICON = new String[]{launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon"}[idx];
            PAGED_VIEW_CELL_LAYOUT = new String[]{launcherPackage + "PagedViewCellLayout", "vd", "yo", "zq", "zq"}[idx]; // CellLayout cannot have UNSPECIFIED dimensions" the one with more members
            PAGED_VIEW_WITH_DRAGGABLE_ITEMS = new String[]{launcherPackage + "PagedViewWithDraggableItems", "vl", "yw", "zy", "zy"}[idx]; // AppsCustomizePagedView extends
            APPS_CUSTOMIZE_CELL_LAYOUT = new String[]{launcherPackage + "AppsCustomizeCellLayout", "kw", "yr", "zt", "zt"}[idx]; // "Invalid ContentType" in AppsCostumize - getChildCount
            APPS_CUSTOMIZE_LAYOUT = new String[]{launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout"}[idx]; // Trebuchet only
            APPS_CUSTOMIZE_PAGED_VIEW = new String[]{launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView"}[idx];
            APPS_CUSTOMIZE_TAB_HOST = new String[]{launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost"}[idx];
            APPS_CUSTOMIZE_CONTENT_TYPE = new String[]{APPS_CUSTOMIZE_PAGED_VIEW + "$ContentType", "lf", "oo", "pp", "pp"}[idx];
            WALLPAPER_OFFSET_INTERPOLATOR = new String[]{WORKSPACE + "$WallpaperOffsetInterpolator", "zd", "acp", "adr", "ads"}[idx]; // Error updating wallpaper offset
            FOLDER = new String[]{launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder"}[idx];
            FOLDER_ICON = new String[]{launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon"}[idx];
            HOTSEAT = new String[]{launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat"}[idx];
            START_SETTINGS_ONCLICK = new String[]{ "", "pu", "td", "ue", "ue"}[idx]; // in onCreate first setOnClickListener after in if-clause
            DRAG_SOURCE = new String[]{launcherPackage + "DragSource", "nn", "qw", "rx", "rx"}[idx]; // first parameter in onDragStart of SearchDropTargetBar
            ITEM_INFO = new String[]{launcherPackage + "ItemInfo", "pr", "ta", "ub", "ub"}[idx]; // Item(id=
            APP_INFO = new String[]{launcherPackage + "AppInfo", "kr", "ob", "pc", "pc"}[idx]; // ApplicationInfo
            SHORTCUT_INFO = new String[]{launcherPackage + "ShortcutInfo", "vz", "zl", "aan", "aan"}[idx]; // ShortcutInfo(title=
            SEARCH_DROP_TARGET_BAR = new String[]{launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar"}[idx];
            ICON_CACHE = new String[]{launcherPackage + "IconCache", "pk", "ss", "tt", "tt"}[idx]; // using preloaded icon for
            UTILITIES = new String[]{launcherPackage + "Utilities", "wi", "zu", "aaw", "aaw"}[idx]; // Launcher.Utilities
            CACHE_ENTRY = new String[]{ICON_CACHE + "$CacheEntry", "pl", "st", "tu", "tu"}[idx]; // new HashMap(50)
            LAUNCHER_MODEL = new String[]{launcherPackage + "LauncherModel", "sg", "vq", "ws", "ws"}[idx]; // Error: ItemInfo passed to checkItemInfo doesn't match original
            LOADER_TASK = new String[]{LAUNCHER_MODEL + "$LoaderTask", "tb", "wl", "xn", "xn"}[idx]; // Should not call runBindSynchronousPage
            FOLDER_INFO = new String[]{launcherPackage + "FolderInfo", "oz", "sh", "ti", "ti"}[idx]; // FolderInfo(id=
            LAUNCHER_APP_STATE = new String[]{launcherPackage + "LauncherAppState", "rr", "vb", "wd", "wd"}[idx]; // "setLauncher() called before init()"
            APP_WIDGET_RESIZE_FRAME = new String[]{launcherPackage + "AppWidgetResizeFrame", "ks", "oc", "pd", "pd"}[idx]; // in AppsCustomizePagedView first line below "if (Build.VERSION.SDK_INT >= 17)"
            ITEM_CONFIGURATION = new String[]{CELL_LAYOUT + "$ItemConfiguration", "ma", "pj", "qk", "qk"}[idx]; // in CellLayout Math.abs(paramArrayOfInt[0]) last interface parameter
            LAUNCHER_APPWIDGET_INFO = new String[]{launcherPackage + "LauncherAppWidgetInfo", "rv", "vf", "wh", "wh"}[idx]; // AppWidget(id=
            DRAG_LAYER = new String[]{launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer"}[idx];
            LAUNCHER_APP_WIDGET_HOST_VIEW = new String[]{launcherPackage + "LauncherAppWidgetHostView", "ru", "ve", "wg", "wg"}[idx]; // in Workspace "getAppWidgetInfo"
            BUBBLE_TEXT_VIEW = new String[]{launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView"}[idx];
            USER_HANDLE = new String[]{"", "", "adl", "aen", "aeo"}[idx]; // last parameter in IconCache "cacheLocked"
            ADB = new String[]{"", "", "adb", "aed", "aee"}[idx];
            GEL = new String[]{"com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL"}[idx];
            NOW_OVERLAY = new String[]{"com.google.android.sidekick.shared.client.NowOverlay", "dzk", "enc", "evx", "evx"}[idx]; // now_overlay:views_hidden_for_search
            SEARCH_OVERLAY_IMPL = new String[]{"com.google.android.search.gel.SearchOverlayImpl", "ccu", "cmh", "cuc", "cuc"}[idx]; // hammerhead
            GSA_CONFIG_FLAGS = new String[]{"com.google.android.search.core.GsaConfigFlags", "ayc", "bgr", "bnj", "bnj"}[idx]; // Unknown string array encoding
            RECOGNIZER_VIEW = new String[]{"com.google.android.search.shared.ui.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView"}[idx];
            SEARCH_PLATE = new String[]{"com.google.android.search.shared.ui.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate"}[idx];
            TRANSITIONS_MANAGER = new String[]{"com.google.android.search.shared.ui.SearchPlate$TransitionsManager", "cen", "cog", "cwb", "cwb"}[idx]; // in SearchPlate: "(this, this);"
            GEL_SEARCH_PLATE_CONTAINER = new String[]{"com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer"}[idx];
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
                LauncherAppState,
                DynamicGrid,
                AppsCustomizeCellLayout;

        public static void hookAllClasses(LoadPackageParam lpparam) {
            Launcher = findClass(ClassNames.LAUNCHER, lpparam.classLoader);
            Workspace = findClass(ClassNames.WORKSPACE, lpparam.classLoader);
            AppsCustomizePagedView = findClass(ClassNames.APPS_CUSTOMIZE_PAGED_VIEW, lpparam.classLoader);
            AppsCustomizeCellLayout = findClass(ClassNames.APPS_CUSTOMIZE_CELL_LAYOUT, lpparam.classLoader);
            CellLayout = findClass(ClassNames.CELL_LAYOUT, lpparam.classLoader);
            CellLayoutLayoutParams = findClass(ClassNames.CELL_LAYOUT_LAYOUT_PARAMS, lpparam.classLoader);
            WallpaperOffsetInterpolator = findClass(ClassNames.WALLPAPER_OFFSET_INTERPOLATOR, lpparam.classLoader);
            PagedViewIcon = findClass(ClassNames.PAGED_VIEW_ICON, lpparam.classLoader);
            DeviceProfile = findClass(ClassNames.DEVICE_PROFILE, lpparam.classLoader);
            DynamicGrid = findClass(ClassNames.DYNAMIC_GRID, lpparam.classLoader);
            AppInfo = findClass(ClassNames.APP_INFO, lpparam.classLoader);

            if (Common.IS_TREBUCHET) {
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

        public static String pviApplyFromApplicationInfo,
                lGetApplicationContext,
                //lIsRotationEnabled,
                clAddViewToCellLayout,
                clSetIsHotseat,
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
                lHideAppsCustomizeHelper,
                lShowWorkspace,
                wMoveToDefaultScreen,
                btvSetShadowsEnabled,
                wOverScroll,
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
                wGetViewForTag,
                wGetScreenWithId,
                wGetFolderForTag,
                lCloseFolder,
                lCloseFolderWParam,
                acthOnTabChanged,
                pvSetCurrentPage,
                acpvOverScroll,
                dpUpdateFromConfiguration,
                dgGetDeviceProfile,
                acthSetInsets,
                wSnapToPage,
                soiSetSearchStarted,
                noOnShow,
                wOnLauncherTransitionEnd,
                fiAdd,
                fiAddItem,
                fiRemove,
                sawMeasureChild,
                fGetItemsInReadingOrder,
                fBind,
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
                acpvUpdatePageCounts,
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
                lasIsDisableAllApps,
                acpvSyncAppsPageItems,
                acpvSetContentType,
                acpvInvalidatePageData,
                acpvSyncPages,
                pvIsLayoutRtl,
                pvGetPageAt,
                acpvEnableHwLayersOnVisiblePages,
                lCreateAppInfo,
                aiMakeShortcut,
                lmGetAppNameComparator,
                acthSetContentTypeImmediate,
                wGetWorkspaceAndHotseatCellLayouts,
                fiFromXml,
                acpvSetAllAppsPadding;

        public static void initMethodNames(int idx) {
            lGetApplicationContext = new String[]{"getApplicationContext", "getApplicationContext", "getApplicationContext", "getApplicationContext", "getApplicationContext"}[idx];
            //lIsRotationEnabled = new String[]{"isRotationEnabled", "gC", "hr", "hA"}[idx]; // getBoolean - single line method
            lGetSearchbar = new String[]{"getSearchBar", "fZ", "gO", "gX", "gY"}[idx]; // "public final SearchDropTargetBar" in Launcher
            lGetQsbBar = new String[]{"getQsbBar", "gw", "hl", "hu", "hv"}[idx]; // public View
            lHasCustomContentToLeft = new String[]{"hasCustomContentToLeft", "fL", "gA", "gJ", "gK"}[idx]; // "()) || (!" under isEmpty
            lHideAppsCustomizeHelper = new String[]{"hideAppsCustomizeHelper", "a", "a", "a", "a"}[idx];
            lShowWorkspace = new String[]{"showWorkspace", "a", "a", "a", "a"}[idx]; // boolean paramBoolean, Runnable paramRunnable
            //lShowAllApps = new String[]{"showAllApps", "a", "a", "a"}[idx];
            lIsAllAppsVisible = new String[]{"isAllAppsVisible", "gs", "hh", "hq", "hr"}[idx]; // onBackPressed first method call
            lFinishBindingItems = new String[]{"finishBindingItems", "U", "Z", "Z", "Z"}[idx]; // hasFocus()
            lOpenFolder = new String[]{"openFolder", "i", "i", "i", "i"}[idx]; // "Opening folder ("
            lCloseFolder = new String[]{"closeFolder", "gr", "hg", "hp", "hq"}[idx]; // if (localFolder != null)
            lCloseFolderWParam = new String[]{"closeFolder", "h", "h", "h", "h"}[idx]; // if ((ViewGroup)paramFolder.getParent().getParent() != null)
            lBindAppsUpdated = new String[]{"bindAppsUpdated", "l", "l", "l", "l"}[idx]; // "(this, paramArrayList), false));"
            lSetWorkspaceBackground = new String[]{"setWorkspaceBackground", "N", "S", "S", "S"}[idx]; // setBackground
            lGetDragLayer = new String[]{"getDragLayer", "fV", "gK", "gT", "gU"}[idx]; // public final DragLayer
            lCreateAppInfo = new String[]{"", "e", "d", "d", "d"}[idx]; // (Intent paramIntent)
            clAttemptPushInDirection = new String[]{"attemptPushInDirection", "b", "b", "b", "b"}[idx]; // "if (Math.abs(paramArrayOfInt[0]) + Math.abs(paramArrayOfInt[1]) > 1)"
            clMarkCellsForView = new String[]{"markCellsForView", "a", "a", "a", "a"}[idx]; // int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean[][] paramArrayOfBoolean, boolean paramBoolean
            clGetShortcutsAndWidgets = new String[]{"getShortcutsAndWidgets", "dH", "ew", "eF", "eF"}[idx]; // getChildCount() > 0
            clAddViewToCellLayout = new String[]{"addViewToCellLayout", "a", "a", "a", "a"}[idx]; // View paramView, int paramInt1, int paramInt2, CellLayout.LayoutParams paramLayoutParams, boolean paramBoolean
            clSetIsHotseat= new String[]{"setIsHotseat", "D", "G", "G", "G"}[idx]; // two line in a row with " = true;"
            wStartDrag = new String[]{"startDrag", "a", "a", "a", "a"}[idx]; // isInTouchMode
            wMoveToDefaultScreen = new String[]{"moveToDefaultScreen", "ao", "at", "at", "at"}[idx]; // Launcher onNewIntent method call of workspace member with (true)
            wOverScroll = new String[]{"overScroll", "g", "g", "g", "g"}[idx]; // (float paramFloat)
            wGetOpenFolder = new String[]{"getOpenFolder", "jp", "kj", "kn", "ks"}[idx]; // localDragLayer.getChildCount();
            wIsOnOrMovingToCustomContent = new String[]{"isOnOrMovingToCustomContent", "jJ", "kE", "kI", "kN"}[idx]; // last if-clause in Launcher onResume
            wEnterOverviewMode = new String[]{"enterOverviewMode", "jO", "kJ", "kN", "kS"}[idx]; // "(true, -1, true);"
            wMoveToCustomContentScreen = new String[]{"moveToCustomContentScreen", "ap", "au", "au", "au"}[idx]; // "View localView = getChildAt(i);" with "-301L"
            wSnapToPage = new String[]{"snapToPage", "bc", "bs", "bt", "bv"}[idx]; // in PagedView requestChildFocus - last line of method
            wOnDragEnd = new String[]{"onDragEnd", "dt", "ei", "er", "er"}[idx]; // only method without interface parameters with InstallShortcutReceiver
            //wOnDragStart = new String[]{"onDragStart", "a", "a", "a"}[idx]; // only method with interface parameters with InstallShortcutReceiver
            wOnLauncherTransitionEnd = new String[]{"onLauncherTransitionEnd", "a", "a", "a", "a"}[idx]; // (Launcher paramLauncher, boolean paramBoolean1, boolean paramBoolean2)
            wOnTransitionPrepare = new String[]{"onTransitionPrepare", "jR", "kM", "kR", "kW"}[idx]; // Method with "if ((bool) && (" in it
            wGetWorkspaceAndHotseatCellLayouts = new String[]{"getWorkspaceAndHotseatCellLayouts", "ka", "kV", "la", "lf"}[idx]; // localArrayList.add((CellLayout)getChildAt(j));
            wGetViewForTag = new String[]{"getViewForTag", "I", "V", "V", "V"}[idx]; // View[] arrayOfView = new View[1];
            wGetScreenWithId = new String[]{"getScreenWithId", "j", "j", "j", "j"}[idx]; // public final CellLayout
            wGetFolderForTag = new String[]{"getFolderForTag", "H", "U", "U", "U"}[idx]; // Folder[] arrayOfFolder = new Folder[1];
            pvPageBeginMoving = new String[]{"pageBeginMoving", "ii", "iY", "jb", "jc"}[idx]; // above "awakenScrollBars"
            pvPageEndMoving = new String[]{"pageEndMoving", "ij", "iZ", "jc", "jd"}[idx]; // method above "accessibility"
            pvSnapToPage = new String[]{"snapToPage", "a", "a", "a", "a"}[idx]; // int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, TimeInterpolator paramTimeInterpolator
            pvGetPageAt = new String[]{"getPageAt", "at", "aJ", "aJ", "aJ"}[idx]; // return getChildAt(paramInt);
            pviApplyFromApplicationInfo = new String[]{"applyFromApplicationInfo", "a", "a", "a", "a"}[idx];
            pvSetCurrentPage = new String[]{"setCurrentPage", "aV", "bl", "bm", "bo"}[idx]; // in PagedView "if (getChildCount() == 0)"
            pvIsLayoutRtl = new String[]{"isLayoutRtl", "hX", "iN", "iQ", "iR"}[idx]; // "getLayoutDirection() == 1"
            sdtbOnDragStart = new String[]{"onDragStart", "a", "a", "a", "a"}[idx]; // twice .start in the method
            sdtbOnDragEnd = new String[]{"onDragEnd", "dt", "ei", "er", "er"}[idx]; // twice .reverse
            btvSetShadowsEnabled = new String[]{"setShadowsEnabled", "w", "z", "z", "z"}[idx]; // invalidate
            btvCreateGlowingOutline = new String[]{"createGlowingOutline", "a", "a", "a", "a"}[idx]; // setBitmap
            acpvOnPackagesUpdated = new String[]{"onPackagesUpdated", "a", "a", "a", "a"}[idx]; // "can not fit on this device"
            acpvOverScroll = new String[]{"overScroll", "g", "g", "g", "g"}[idx]; // (float paramFloat)
            acpvSetApps = new String[]{"setApps", "b", "b", "b", "b"}[idx]; // Collections.sort
            acpvUpdateApps = new String[]{"updateApps", "g", "g", "g", "g"}[idx]; // in BindAppsUpdated in Launcher
            acpvRemoveApps = new String[]{"removeApps", "f", "f", "f", "f"}[idx]; // in Launcher removeApps."(paramArrayList2)"
            acpvEnableHwLayersOnVisiblePages = new String[]{"enableHwLayersOnVisiblePages", "db", "dQ", "dZ", "dZ"}[idx]; // "localArrayList2.add(" between break; and return;
            //acpvGetTabHost = new String[]{"getTabHost", "de", "dt", "ec"}[idx];
            acpvSyncAppsPageItems = new String[]{"syncAppsPageItems", "aq", "aG", "aG", "aG"}[idx]; // int k = Math.min(i + j, this.
            acpvSetContentType = new String[]{"setContentType", "a", "a", "a", "a"}[idx];
            acpvInvalidatePageData = new String[]{"invalidatePageData", "j", "k", "k", "k"}[idx]; // method for "(i, true);"
            acpvSyncPages = new String[]{"syncPages", "da", "dP", "dY", "dY"}[idx]; // removeAllViews
            acpvBeginDragging = new String[]{"beginDragging", "n", "n", "n", "n"}[idx]; // "instanceof PagedViewIcon" in AppsCustomizePagedView
            acpvUpdatePageCounts = new String[]{"updatePageCounts", "cO", "dD", "dM", "dM"}[idx]; // (int)Math.ceil
            acpvSetAllAppsPadding = new String[]{"setAllAppsPadding", "b", "b", "b", "b"}[idx]; // .set(paramRect);
            acthOnTabChanged = new String[]{"onTabChanged", "c", "c", "c", "c"}[idx]; // setBackgroundColor
            acthSetInsets = new String[]{"setInsets", "c", "c", "c", "c"}[idx]; // (Rect
            acthGetContentTypeForTabTag = new String[]{"getContentTypeForTabTag", "j", "r", "r", "q"}[idx]; // (String paramString)
            acthSetContentTypeImmediate = new String[]{"setContentTypeImmediate", "b", "b", "b", "b"}[idx]; // setOnTabChangedListener(null)
            dpGetWorkspacePadding = new String[]{"getWorkspacePadding", "aC", "aS", "aS", "aS"}[idx]; // Rect localRect2 = new Rect();
            dpUpdateFromConfiguration = new String[]{"updateFromConfiguration", "a", "a", "a", "a"}[idx]; // float paramFloat, int paramInt, Resources paramResources, DisplayMetrics paramDisplayMetrics
            dgGetDeviceProfile = new String[]{"getDeviceProfile", "eV", "fK", "fT", "fT"}[idx]; // public final
            //fOnRemove = new String[]{"onRemove", "g", "g", "g"}[idx]; // removeView(localView)
            //fOnAdd = new String[]{"onAdd", "f", "f", "f"}[idx]; // (1 + getItemCount()); - first line  = new String[]true
            //fReplaceFolderWithFinalItem = new String[]{"replaceFolderWithFinalItem", "ge", "ge", "gn"}[idx]; // if (localView != new String[]null)
            fGetItemsInReadingOrder = new String[]{"getItemsInReadingOrder", "fr", "gh", "gq", "gr"}[idx]; // public final ArrayList
            fBind = new String[]{"bind", "a", "a", "a", "a"}[idx]; // ", boolean paramBoolean1, boolean paramBoolean2)"
            fiAdd = new String[]{"add", "j", "j", "j", "j"}[idx]; // FolderInfo - .add
            fiAddItem = new String[]{"addItem", "i", "i", "i", "i"}[idx]; // FolderIcon - below "getVisibility() == 0;" with interface parameter ShortcutInfo
            fiRemove = new String[]{"remove", "k", "k", "k", "k"}[idx]; // FolderInfo - .remove
            fiFromXml = new String[]{"fromXml", "a", "a", "a", "a"}[idx]; // FolderIcon - method with ".topMargin" in it
            sawMeasureChild = new String[]{"measureChild", "M", "M", "M", "M"}[idx]; // in Launcher above "return localFolderIcon"
            siGetIntent = new String[]{"getIntent", "getIntent", "getIntent", "getIntent", "getIntent"}[idx];
            siGetIcon = new String[]{"getIcon", "a", "a", "a", "a"}[idx]; // public final Bitmap
            aiMakeShortcut = new String[]{"makeShortcut", "cE", "dt", "dC", "dC"}[idx]; // (this);
            icGetFullResIcon = new String[]{"getFullResIcon", "a", "a", "a", "a"}[idx]; // (Resources paramResources, int paramInt)
            icCacheLocked = new String[]{"cacheLocked", "b", "a", "a", "a"}[idx];
            uCreateIconBitmap = new String[]{"createIconBitmap", "a", "a", "a", "a"}[idx]; // (Drawable paramDrawable, Context paramContext)
            lmCheckItemPlacement = new String[]{"checkItemPlacement", "a", "a", "a", "a"}[idx]; // "Error loading shortcut into "
            lmIsShortcutInfoUpdateable = new String[]{"isShortcutInfoUpdateable", "e", "e", "e", "e"}[idx]; // "android.intent.action.MAIN"
            lmDeleteItemFromDatabase = new String[]{"deleteItemFromDatabase", "b", "b", "b", "b"}[idx]; // (Context paramContext, ItemInfo paramta) - link to "deleting a folder"
            lmDeleteFolderContentsFromDatabase = new String[]{"deleteFolderContentsFromDatabase", "a", "a", "a", "a"}[idx]; // (Context paramContext, FolderInfo paramsh)
            lmGetAppNameComparator = new String[]{"getAppNameComparator", "hw", "im", "iq", "ir"}[idx]; // public static final Comparator
            dlAddResizeFrame = new String[]{"addResizeFrame", "a", "a", "a", "a"}[idx]; // ", CellLayout paramCellLayout)"
            gsaShouldAlwaysShowHotwordHint = new String[]{"shouldAlwaysShowHotwordHint", "uK", "xE", "yB", "yG"}[idx]; // always_show_hotword_hint - method below "'&', '='"
            soiSetSearchStarted = new String[]{"setSearchStarted", "cs", "cI", "cI", "cI"}[idx]; // onResume before cancel()
            noOnShow = new String[]{"onShow", "p", "u", "v", "v"}[idx]; // boolean paramBoolean1, boolean paramBoolean2
            woiSyncWithScroll = new String[]{"syncWithScroll", "kf", "la", "lf", "lk"}[idx]; // computeScroll in Workspace
            rvCanShowHotwordAnimation = new String[]{"canShowHotwordAnimation", "NH", "Se", "UC", "UH"}[idx]; // == new String[]5
            spSetProximityToNow = new String[]{"setProximityToNow", "x", "x", "x", "x"}[idx]; // (float paramFloat) with RecognizerView
            tmSetTransitionsEnabled = new String[]{"setTransitionsEnabled", "cG", "cY", "cZ", "cZ"}[idx]; // (4)
            uIsL = new String[]{"", "", "jO", "jS", "jV"}[idx];
            lasIsDisableAllApps = new String[]{"isDisableAllApps", "ha", "hS", "hW", "hX"}[idx]; // launcher_noallapps
        }
    }

    public static class Fields {

        public static String dpHotseatAllAppsRank,
                dpNumHotseatIcons,
                cllpCanReorder,
                sdtbIsSearchBarHidden,
                sdtbQsbBar,
                wCustomContentShowing,
                pvCurrentPage,
                lHotseat,
                lAppsCustomizeTabHost,
                acthInTransition,
                wState,
                wDefaultPage,
                btvShadowsEnabled,
                fiPreviewBackground,
                fiFolderName,
                fFolderEditText,
                fFolderInfo,
                fiFolderInfo,
                fiFolder,
                fiContents,
                fiOpened,
                acpvContentType,
                pvIsPageMoving,
                wIsSwitchingState,
                dpHotseatBarHeightPx,
                lState,
                wTouchState,
                pvNextPage,
                lHasFocus,
                lPaused,
                aiComponentName,
                acpvAllAppsNumCols,
                acpvAllAppsNumRows,
                pvPageIndicator,
                acthContent,
                dpPageIndicatorHeightPx,
                lAppsCustomizePagedView,
                iiID,
                iiItemType,
                iiScreenId,
                iiContainer,
                ceIcon,
                lIconCache,
                fiLongPressHelper,
                clphHasPerformedLongPress,
                lawiProviderName,
                fMaxCountY,
                fMaxNumItems,
                acthAppsCustomizePane,
                acpvNumAppsPages,
                acpvCellCountX,
                acpvCellCountY,
                acpvRemoveAllViewsOnPage,
                uIconWidth,
                uIconHeight,
                acpvAllApps,
                clIsHotseat,
                clShortcutsAndWidgets,
                sawIsHotseat,
                dpIconTextSize,
                acpvContentHeight,
                dpAllAppsIconSize,
                fContent,
                dpIconDrawablePaddingPx;

        public static void initFieldNames(int idx) {
            dpHotseatAllAppsRank = new String[]{"hotseatAllAppsRank", "zp", "BQ", "Cv", "Cu"}[idx]; // only / 2 operation
            dpNumHotseatIcons = new String[]{"numHotseatIcons", "yz", "AY", "BD", "BC"}[idx]; // toString of DynamicGrid ", hc: "
            dpHotseatBarHeightPx = new String[]{"hotseatBarHeightPx", "zo", "BP", "Cu", "Ct"}[idx]; // 4 * ...
            dpPageIndicatorHeightPx = new String[]{"pageIndicatorHeightPx", "zw", "BX", "CC", "CB"}[idx]; // last parameter in last localRect2.set(
            dpIconTextSize = new String[]{"allAppsIconTextSizePx", "zd", "BE", "Cj", "Ci"}[idx]; // PagedViewIcon setTextSize
            dpAllAppsIconSize = new String[]{"allAppsIconSizePx", "zc", "BD", "Ci", "Ch"}[idx]; // first in acpv "public final float"
            dpIconDrawablePaddingPx = new String[]{"iconDrawablePaddingPx", "zZ", "BA", "Cf", "Ce"}[idx]; // in BubbleTextView setCompoundDrawablePadding
            clphHasPerformedLongPress = new String[]{"mHasPerformedLongPress", "wG", "zf", "zK", "zJ"}[idx]; // only boolean member
            cllpCanReorder = new String[]{"canReorder", "wf", "yE", "zj", "zi"}[idx]; // second member with "= true"
            clIsHotseat = new String[]{"mIsHotseat", "vq", "xP", "yu", "yt"}[idx];
            clShortcutsAndWidgets = new String[]{"mShortcutsAndWidgets", "vp", "xO", "yt", "ys"}[idx];
            sawIsHotseat = new String[]{"mIsHotseatLayout", "Ng", "PQ", "Qr", "Qu"}[idx];
            sdtbIsSearchBarHidden = new String[]{"mIsSearchBarHidden", "MV", "PF", "Qg", "Qj"}[idx]; // above Qsb member
            sdtbQsbBar = new String[]{"mQSBSearchBar", "MW", "PG", "Qh", "Qk"}[idx];
            wCustomContentShowing = new String[]{"mCustomContentShowing", "PV", "SH", "Ti", "Ti"}[idx]; // "() == 0) || (!this."
            wState = new String[]{"mState", "Qj", "SV", "Tw", "Tw"}[idx]; // WorkspaceState member
            wDefaultPage = new String[]{"mDefaultPage", "PI", "Su", "SV", "SV"}[idx];  // "Expected custom content screen to exist", member gets decreased by one // "(-1 + this."
            wTouchState = new String[]{"mTouchState", "KY", "NF", "Oj", "On"}[idx]; // onInterceptTouchEvent while clause
            wIsSwitchingState = new String[]{"mIsSwitchingState", "Qk", "SW", "Tx", "Tx"}[idx]; // start from onTouch, second method call in if-clause
            lHotseat = new String[]{"mHotseat", "EO", "Hu", "HZ", "Id"}[idx];
            lAppsCustomizeTabHost = new String[]{"mAppsCustomizeTabHost", "ER", "Hx", "Ic", "Ig"}[idx];
            lIconCache = new String[]{"mIconCache", "rF", "uf", "uK", "uK"}[idx]; // IconCache member in Launcher
            lState = new String[]{"mState", "Et", "GZ", "HE", "HH"}[idx]; // onNewIntent - "if ((i != 0) && (this."
            lHasFocus = new String[]{"mHasFocus", "Fj", "HP", "It", "Ix"}[idx]; // onWindowFocusChanged
            lPaused = new String[]{"mPaused", "EZ", "HF", "Ik", "Io"}[idx]; // only boolean assignement in onPause()
            lAppsCustomizePagedView = new String[]{"mAppsCustomizeContent", "ES", "Hy", "Id", "Ih"}[idx]; // AppsCustomizePagedView in Launcher
            btvShadowsEnabled = new String[]{"mShadowsEnabled", "ue", "wF", "xk", "xj"}[idx]; // only boolean member = true
            fiPreviewBackground = new String[]{"mPreviewBackground", "CE", "Fh", "FM", "FP"}[idx]; // FOLDERICON - only ImageView member
            fiFolderName = new String[]{"mFolderName", "CF", "Fi", "FN", "FQ"}[idx]; // FOLDERICON - only BubbleTextView
            fiFolder = new String[]{"mFolder", "CB", "Fe", "FJ", "FM"}[idx]; // FOLDERICON - only Folder member
            fiLongPressHelper = new String[]{"mLongPressHelper", "ui", "wJ", "xo", "xn"}[idx]; // cancelLongPress
            fFolderInfo = new String[]{"mInfo", "BF", "Ej", "EO", "ER"}[idx]; // <mInfo>.title))
            fiFolderInfo = fFolderInfo; // FolderIcon - same as fFolderInfo
            fiContents = new String[]{"contents", "Dt", "FW", "GB", "GE"}[idx]; // first ArrayList in FolderInfo
            fiOpened = new String[]{"opened", "Ds", "FV", "GA", "GD"}[idx]; // only boolean member
            //fFolderIcon = new String[]{"mFolderIcon", "BL", "Ep", "EU"}[idx]; // only FolderIcon member
            fFolderEditText = new String[]{"mFolderName", "Cf", "EJ", "Fo", "Fr"}[idx]; // only FolderEditText member
            //fMaxCountX = new String[]{"mMaxCountX", "BM", "Eq", "EV"}[idx]; // Folder constructor, last line - maxNumItems = X * Y;
            fMaxCountY = new String[]{"mMaxCountY", "BN", "Er", "EW", "EZ"}[idx]; // Folder constructor, last line - maxNumItems = X * Y;
            fMaxNumItems = new String[]{"mMaxNumItems", "BO", "Es", "EX", "Fa"}[idx]; // Folder constructor, last line - maxNumItems = X * Y;
            fContent = new String[]{"mContent", "BH", "El", "EQ", "ET"}[idx]; // only CellLayout member
            pvIsPageMoving = new String[]{"mIsPageMoving", "Lv", "Oc", "OG", "OK"}[idx];  // "while (!this."
            pvNextPage = new String[]{"mNextPage", "KI", "Np", "NT", "NX"}[idx]; // first protected int = -1
            pvPageIndicator = new String[]{"mPageIndicator", "Lz", "Og", "OK", "OO"}[idx]; // setContentDescription
            pvCurrentPage = new String[]{"mCurrentPage", "KF", "Nm", "NQ", "NU"}[idx]; // in OnTouch -> "indexOfChild(paramView) != this."
            aiComponentName = new String[]{"componentName", "rJ", "uj", "uO", "uO"}[idx]; // only ComponentName member
            iiItemType = new String[]{"itemType", "En", "GT", "Hy", "HB"}[idx]; // Item(id=
            iiID = new String[]{"id", "id", "id", "id", "id"}[idx];
            iiScreenId = new String[]{"screenId", "vO", "yn", "yS", "yR"}[idx];
            iiContainer = new String[]{"container", "Eo", "GU", "Hz", "HC"}[idx];
            //iiTitle = new String[]{"title", "title", "title", "title"}[idx];
            ceIcon = new String[]{"icon", "DZ", "GE", "Hj", "Hm"}[idx];
            lawiProviderName = new String[]{"providerName", "GX", "JF", "Kj", "Kn"}[idx]; // only ComponentName member
            acthInTransition = new String[]{"mInTransition", "tf", "vF", "wk", "wk"}[idx]; // onInterceptTouchEvent first member in if-clause
            acthContent = new String[]{"mContent", "tD", "wd", "wI", "wI"}[idx]; // .getLayoutParams in setInsets
            //acthTabsContainer = new String[]{"mTabsContainer", "tA", "wA", "wF"}[idx]; // setAlpha
            acthAppsCustomizePane = new String[]{"mAppsCustomizePane", "tB", "wb", "wG", "wG"}[idx]; // setAlpha
            uIconWidth = new String[]{"sIconWidth", "NC", "Qm", "QN", "QS"}[idx]; // first private static int
            uIconHeight = new String[]{"sIconHeight", "ND", "Qn", "QO", "QT"}[idx]; // second private static int
            acpvAllAppsNumCols = new String[]{"allAppsNumCols", "zr", "BS", "Cx", "Cw"}[idx]; // onMeasure localDeviceProfile
            acpvAllAppsNumRows = new String[]{"allAppsNumRows", "zq", "BR", "Cw", "Cv"}[idx]; // onMeasure localDeviceProfile
            acpvAllApps = new String[]{"mApps", "sA", "va", "vF", "vF"}[idx]; // sort
            //acpvAllWidgets = new String[]{"mWidgets", "sB", "vb", "vG"}[idx]; // 2nd "isEmpty"
            acpvNumAppsPages = new String[]{"mNumAppsPages", "sN", "vn", "vS", "vS"}[idx]; // Math.ceil
            acpvCellCountX = new String[]{"mCellCountX", "Lg", "NN", "Or", "Ov"}[idx]; // Math.ceil
            acpvCellCountY = new String[]{"mCellCountY", "Lh", "NO", "Os", "Ow"}[idx]; // Math.ceil
            acpvRemoveAllViewsOnPage = new String[]{"removeAllViewsOnPage", "cI", "dx", "dG", "dG"}[idx]; // ")localView)."
            acpvContentType = new String[]{"mContentType", "sw", "uW", "vB", "vB"}[idx]; // private oo uW = oo.vW;
            acpvContentHeight = new String[]{"mContentHeight", "sH", "vh", "vM", "vM"}[idx]; // second View.MeasureSpec.makeMeasureSpec(this.
        }
    }
}