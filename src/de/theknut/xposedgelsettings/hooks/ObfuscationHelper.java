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

            LAUNCHER = new String[]{launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher", launcherPackage + "Launcher"}[idx];
            WORKSPACE = new String[]{launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace", launcherPackage + "Workspace"}[idx];
            WORKSPACE_STATE = new String[]{WORKSPACE + "$State", "zc", "aco", "adq"}[idx];
            DEVICE_PROFILE = new String[]{launcherPackage + "DeviceProfile", "mz", "qi", "rj"}[idx]; // All Device Profiles must have
            CELL_LAYOUT = new String[]{launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout", launcherPackage + "CellLayout"}[idx];
            CELL_LAYOUT_CELL_INFO = new String[]{CELL_LAYOUT + "$CellInfo", "lz", "pi", "qj"}[idx]; // Cell[=view
            CELL_LAYOUT_LAYOUT_PARAMS = new String[]{CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams", CELL_LAYOUT + "$LayoutParams"}[idx];
            PAGED_VIEW = new String[]{launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView", launcherPackage + "PagedView"}[idx];
            PAGED_VIEW_ICON = new String[]{launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon", launcherPackage + "PagedViewIcon"}[idx];
            PAGED_VIEW_CELL_LAYOUT = new String[]{launcherPackage + "PagedViewCellLayout", "vd", "yo", "zq"}[idx]; // CellLayout cannot have UNSPECIFIED dimensions" the one with more members
            PAGED_VIEW_WITH_DRAGGABLE_ITEMS = new String[]{launcherPackage + "PagedViewWithDraggableItems", "vl", "yw", "zy"}[idx]; // AppsCustomizePagedView extends
            APPS_CUSTOMIZE_CELL_LAYOUT = new String[]{launcherPackage + "AppsCustomizeCellLayout", "kw", "yr", "zt"}[idx]; // "Invalid ContentType" in AppsCostumize - getChildCount
            APPS_CUSTOMIZE_LAYOUT = new String[]{launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout", launcherPackage + "AppsCustomizeLayout"}[idx]; // Trebuchet only
            APPS_CUSTOMIZE_PAGED_VIEW = new String[]{launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView", launcherPackage + "AppsCustomizePagedView"}[idx];
            APPS_CUSTOMIZE_TAB_HOST = new String[]{launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost", launcherPackage + "AppsCustomizeTabHost"}[idx];
            APPS_CUSTOMIZE_CONTENT_TYPE = new String[]{APPS_CUSTOMIZE_PAGED_VIEW + "$ContentType", "lf", "oo", "pp"}[idx];
            WALLPAPER_OFFSET_INTERPOLATOR = new String[]{WORKSPACE + "$WallpaperOffsetInterpolator", "zd", "acp", "adr"}[idx]; // Error updating wallpaper offset
            FOLDER = new String[]{launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder", launcherPackage + "Folder"}[idx];
            FOLDER_ICON = new String[]{launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon", launcherPackage + "FolderIcon"}[idx];
            HOTSEAT = new String[]{launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat", launcherPackage + "Hotseat"}[idx];
            START_SETTINGS_ONCLICK = new String[]{ "", "pu", "td", "ue"}[idx]; // in onCreate first setOnClickListener after in if-clause
            DRAG_SOURCE = new String[]{launcherPackage + "DragSource", "nn", "qw", "rx"}[idx]; // first parameter in onDragStart of SearchDropTargetBar
            ITEM_INFO = new String[]{launcherPackage + "ItemInfo", "pr", "ta", "ub"}[idx]; // Item(id=
            APP_INFO = new String[]{launcherPackage + "AppInfo", "kr", "ob", "pc"}[idx]; // firstInstallTime=
            SHORTCUT_INFO = new String[]{launcherPackage + "ShortcutInfo", "vz", "zl", "aan"}[idx]; // ShortcutInfo(title=
            SEARCH_DROP_TARGET_BAR = new String[]{launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar", launcherPackage + "SearchDropTargetBar"}[idx];
            ICON_CACHE = new String[]{launcherPackage + "IconCache", "pk", "ss", "tt"}[idx]; // using preloaded icon for
            UTILITIES = new String[]{launcherPackage + "Utilities", "wi", "zu", "aaw"}[idx]; // Launcher.Utilities
            CACHE_ENTRY = new String[]{ICON_CACHE + "$CacheEntry", "pl", "st", "tu"}[idx]; // new HashMap(50)
            LAUNCHER_MODEL = new String[]{launcherPackage + "LauncherModel", "sg", "vq", "ws"}[idx]; // Error: ItemInfo passed to checkItemInfo doesn't match original
            LOADER_TASK = new String[]{LAUNCHER_MODEL + "$LoaderTask", "tb", "wl", "xn"}[idx]; // Should not call runBindSynchronousPage
            FOLDER_INFO = new String[]{launcherPackage + "FolderInfo", "oz", "sh", "ti"}[idx]; // FolderInfo(id=
            LAUNCHER_APP_STATE = new String[]{launcherPackage + "LauncherAppState", "rr", "vb", "wd"}[idx]; // Folder onMeasure
            APP_WIDGET_RESIZE_FRAME = new String[]{launcherPackage + "AppWidgetResizeFrame", "ks", "oc", "pd"}[idx]; // in AppsCustomizePagedView search for Bundle its below if (....17)
            ITEM_CONFIGURATION = new String[]{CELL_LAYOUT + "$ItemConfiguration", "ma", "pj", "qk"}[idx]; // in CellLayout Math.abs(paramArrayOfInt[0])
            LAUNCHER_APPWIDGET_INFO = new String[]{launcherPackage + "LauncherAppWidgetInfo", "rv", "vf", "wh"}[idx]; // AppWidget(id=
            DRAG_LAYER = new String[]{launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer", launcherPackage + "DragLayer"}[idx];
            LAUNCHER_APP_WIDGET_HOST_VIEW = new String[]{launcherPackage + "LauncherAppWidgetHostView", "ru", "ve", "wg"}[idx]; // in Workspace "getAppWidgetInfo"
            BUBBLE_TEXT_VIEW = new String[]{launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView", launcherPackage + "BubbleTextView"}[idx];
            USER_HANDLE = new String[]{"", "", "adl", "aen"}[idx]; // last parameter in IconCache "cacheLocked"
            ADB = new String[]{"", "", "adb", "aed"}[idx];
            GEL = new String[]{"com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL", "com.google.android.launcher.GEL"}[idx];
            NOW_OVERLAY = new String[]{"com.google.android.sidekick.shared.client.NowOverlay", "dzk", "enc", "evx"}[idx]; // now_overlay:views_hidden_for_search
            SEARCH_OVERLAY_IMPL = new String[]{"com.google.android.search.gel.SearchOverlayImpl", "ccu", "cmh", "cuc"}[idx]; // hammerhead
            GSA_CONFIG_FLAGS = new String[]{"com.google.android.search.core.GsaConfigFlags", "ayc", "bgr", "bnj"}[idx]; // Unknown string array encoding
            RECOGNIZER_VIEW = new String[]{"com.google.android.search.shared.ui.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView", "com.google.android.search.searchplate.RecognizerView"}[idx];
            SEARCH_PLATE = new String[]{"com.google.android.search.shared.ui.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate", "com.google.android.search.searchplate.SearchPlate"}[idx];
            TRANSITIONS_MANAGER = new String[]{"com.google.android.search.shared.ui.SearchPlate$TransitionsManager", "cen", "cog", "cwb"}[idx]; // onLayout - SearchPlate
            GEL_SEARCH_PLATE_CONTAINER = new String[]{"com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer", "com.google.android.search.gel.GelSearchPlateContainer"}[idx];
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

        public static String pviApplyFromApplicationInfo,
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
                fiAdd,
                fiRemove,
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
                lasIsDisableAllApps,
                acpvSyncAppsPageItems,
                acpvSetContentType,
                acpvInvalidatePageData,
                acpvSyncPages,
                acpvIsLayoutRtl,
                pvGetPageAt,
                acpvEnableHwLayersOnVisiblePages,
                lCreateAppInfo,
                aiMakeShortcut,
                lmGetAppNameComparator,
                acthSetContentTypeImmediate,
                wGetWorkspaceAndHotseatCellLayouts;

        public static void initMethodNames(int idx) {
            lGetApplicationContext = new String[]{"getApplicationContext", "getApplicationContext", "getApplicationContext", "getApplicationContext"}[idx];
            lIsRotationEnabled = new String[]{"isRotationEnabled", "gC", "hr", "hA"}[idx]; // getBoolean - single line method
            lGetSearchbar = new String[]{"getSearchBar", "fZ", "gO", "gX"}[idx]; // return SearchDropTargetBar in Launcher
            lGetQsbBar = new String[]{"getQsbBar", "gw", "hl", "hu"}[idx]; // public View
            lHasCustomContentToLeft = new String[]{"hasCustomContentToLeft", "fL", "gA", "gJ"}[idx]; // "()) || (!" under isEmpty
            lHideAppsCustomizeHelper = new String[]{"hideAppsCustomizeHelper", "a", "a", "a"}[idx];
            lShowWorkspace = new String[]{"showWorkspace", "a", "a", "a"}[idx]; // boolean paramBoolean, Runnable paramRunnable
            //lShowAllApps = new String[]{"showAllApps", "a", "a", "a"}[idx];
            lIsAllAppsVisible = new String[]{"isAllAppsVisible", "gs", "hh", "hq"}[idx]; // onBackPressed first method call
            lFinishBindingItems = new String[]{"finishBindingItems", "U", "Z", "Z"}[idx]; // hasFocus()
            lOpenFolder = new String[]{"openFolder", "i", "i", "i"}[idx]; // "Opening folder ("
            lCloseFolder = new String[]{"closeFolder", "gr", "hg", "hq"}[idx]; // localFolder != new String[]null
            lBindAppsUpdated = new String[]{"bindAppsUpdated", "l", "l", "l"}[idx]; // "(this, paramArrayList), false));"
            lSetWorkspaceBackground = new String[]{"setWorkspaceBackground", "N", "S", "S"}[idx]; // setBackground
            lGetDragLayer = new String[]{"getDragLayer", "fV", "gK", "gT"}[idx]; // public final DragLayer
            lCreateAppInfo = new String[]{"", "e", "d", "d"}[idx];
            clAttemptPushInDirection = new String[]{"attemptPushInDirection", "b", "b", "b"}[idx]; // "if (Math.abs(paramArrayOfInt[0]) + Math.abs(paramArrayOfInt[1]) > 1)"
            clMarkCellsForView = new String[]{"markCellsForView", "a", "a", "a"}[idx]; // int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean[][] paramArrayOfBoolean, boolean paramBoolean
            clGetShortcutsAndWidgets = new String[]{"getShortcutsAndWidgets", "dH", "ew", "eF"}[idx]; // getChildCount() > 0
            clAddViewToCellLayout = new String[]{"addViewToCellLayout", "a", "a", "a"}[idx]; // View paramView, int paramInt1, int paramInt2, CellLayout.LayoutParams paramLayoutParams, boolean paramBoolean
            wStartDrag = new String[]{"startDrag", "a", "a", "a"}[idx]; // isInTouchMode
            wMoveToDefaultScreen = new String[]{"moveToDefaultScreen", "ao", "at", "at"}[idx]; // Launcher onNewIntent method call of workspace member with (true)
            wOverScroll = new String[]{"overScroll", "g", "g", "g"}[idx]; // (float paramFloat)
            wGetOpenFolder = new String[]{"getOpenFolder", "jp", "kj", "kn"}[idx]; // localDragLayer.getChildCount();
            wIsOnOrMovingToCustomContent = new String[]{"isOnOrMovingToCustomContent", "jJ", "kE", "kI"}[idx]; // last if-clause in Launcher onResume
            wEnterOverviewMode = new String[]{"enterOverviewMode", "jO", "kJ", "kN"}[idx]; // "()) || (!this."
            wMoveToCustomContentScreen = new String[]{"moveToCustomContentScreen", "ap", "au", "au"}[idx]; // Workspace "View localView = new String[]getChildAt"
            wSetCurrentPage = new String[]{"setCurrentPage", "aV", "bl", "bm"}[idx];
            wSnapToPage = new String[]{"snapToPage", "bc", "bs", "bt"}[idx]; // in PagedView requestChildFocus
            wOnDragEnd = new String[]{"onDragEnd", "dt", "ei", "er"}[idx]; // only method without interface parameters with InstallShortcutReceiver
            //wOnDragStart = new String[]{"onDragStart", "a", "a", "a"}[idx]; // only method with interface parameters with InstallShortcutReceiver
            wOnLauncherTransitionEnd = new String[]{"onLauncherTransitionEnd", "a", "a", "a"}[idx]; // (Launcher paramLauncher, boolean paramBoolean1, boolean paramBoolean2)
            wOnTransitionPrepare = new String[]{"onTransitionPrepare", "jR", "kM", "kR"}[idx]; // "if ((bool) && ("
            wGetWorkspaceAndHotseatCellLayouts = new String[]{"getWorkspaceAndHotseatCellLayouts", "ka", "kV", "la"}[idx]; // localArrayList.add((CellLayout)getChildAt(j));
            pvPageBeginMoving = new String[]{"pageBeginMoving", "ii", "iY", "jb"}[idx]; // above "awakenScrollBars"
            pvPageEndMoving = new String[]{"pageEndMoving", "ij", "iZ", "jc"}[idx]; // method above "accessibility"
            pvSnapToPage = new String[]{"snapToPage", "a", "a", "a"}[idx]; // int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean, TimeInterpolator paramTimeInterpolator
            pvGetPageAt = new String[]{"getPageAt", "at", "aJ", "aJ"}[idx];
            pviApplyFromApplicationInfo = new String[]{"applyFromApplicationInfo", "a", "a", "a"}[idx];
            sdtbOnDragStart = new String[]{"onDragStart", "a", "a", "a"}[idx]; // twice .start in the method
            sdtbOnDragEnd = new String[]{"onDragEnd", "dt", "ei", "er"}[idx]; // twice .reverse
            btvSetShadowsEnabled = new String[]{"setShadowsEnabled", "w", "z", "z"}[idx]; // invalidate
            btvCreateGlowingOutline = new String[]{"createGlowingOutline", "a", "a", "a"}[idx]; // setBitmap
            acpvOnPackagesUpdated = new String[]{"onPackagesUpdated", "a", "a", "a"}[idx]; // "can not fit on this device"
            acpvOverScroll = new String[]{"overScroll", "g", "g", "g"}[idx]; // (float paramFloat)
            acpvSetCurrentPage = new String[]{"setCurrentPage", "aV", "bl", "bm"}[idx];
            acpvSetApps = new String[]{"setApps", "b", "b", "b"}[idx]; // Collections.sort
            acpvUpdateApps = new String[]{"updateApps", "g", "g", "g"}[idx]; // in BindAppsUpdated in Launcher
            acpvRemoveApps = new String[]{"removeApps", "f", "f", "f"}[idx]; // in Launcher removeApps."(paramArrayList2)"
            acpvEnableHwLayersOnVisiblePages = new String[]{"enableHwLayersOnVisiblePages", "db", "dQ", "dZ"}[idx];
            //acpvGetTabHost = new String[]{"getTabHost", "de", "dt", "ec"}[idx];
            acpvSyncAppsPageItems = new String[]{"syncAppsPageItems", "aq", "aG", "aG"}[idx];
            acpvSetContentType = new String[]{"setContentType", "a", "a", "a"}[idx];
            acpvInvalidatePageData = new String[]{"invalidatePageData", "j", "k", "k"}[idx];
            acpvSyncPages = new String[]{"syncPages", "da", "dP", "dY"}[idx]; // removeAllViews
            acpvIsLayoutRtl = new String[]{"isLayoutRtl", "hX", "iN", "iQ"}[idx];
            acpvBeginDragging = new String[]{"beginDragging", "n", "n", "n"}[idx]; // "instanceof PagedViewIcon" in AppsCustomizePagedView
            acthOnTabChanged = new String[]{"onTabChanged", "c", "c", "c"}[idx]; // setBackgroundColor
            acthSetInsets = new String[]{"setInsets", "c", "c", "c"}[idx]; // (Rect
            acthGetContentTypeForTabTag = new String[]{"getContentTypeForTabTag", "j", "r", "r"}[idx]; // (String paramString)
            acthSetContentTypeImmediate = new String[]{"setContentTypeImmediate", "b", "b", "b"}[idx]; // setOnTabChangedListener(null)
            dpGetWorkspacePadding = new String[]{"getWorkspacePadding", "aC", "aS", "aS"}[idx]; // second method with (int paramInt)
            dpUpdateFromConfiguration = new String[]{"updateFromConfiguration", "a", "a", "a"}[idx]; // float paramFloat, int paramInt, Resources paramResources, DisplayMetrics paramDisplayMetrics
            //fOnRemove = new String[]{"onRemove", "g", "g", "g"}[idx]; // removeView(localView)
            //fOnAdd = new String[]{"onAdd", "f", "f", "f"}[idx]; // (1 + getItemCount()); - first line  = new String[]true
            //fReplaceFolderWithFinalItem = new String[]{"replaceFolderWithFinalItem", "ge", "ge", "gn"}[idx]; // if (localView != new String[]null)
            fGetItemsInReadingOrder = new String[]{"getItemsInReadingOrder", "fr", "gh", "gq"}[idx]; // public final ArrayList
            fiAdd = new String[]{"add", "j", "j", "j"}[idx]; // FolderInfo - .add
            fiRemove = new String[]{"remove", "k", "k", "k"}[idx]; // FolderInfo - .remove
            siGetIntent = new String[]{"getIntent", "getIntent", "getIntent", "getIntent"}[idx];
            siGetIcon = new String[]{"getIcon", "a", "a", "a"}[idx]; // public final Bitmap
            aiMakeShortcut = new String[]{"makeShortcut", "cE", "dt", "dC"}[idx];
            icGetFullResIcon = new String[]{"getFullResIcon", "a", "a", "a"}[idx]; // (Resources paramResources, int paramInt)
            icCacheLocked = new String[]{"cacheLocked", "b", "a", "a"}[idx];
            uCreateIconBitmap = new String[]{"createIconBitmap", "a", "a", "a"}[idx]; // (Drawable paramDrawable, Context paramContext)
            lmCheckItemPlacement = new String[]{"checkItemPlacement", "a", "a", "a"}[idx]; // "Error loading shortcut into "
            lmIsShortcutInfoUpdateable = new String[]{"isShortcutInfoUpdateable", "e", "e", "e"}[idx]; // "android.intent.action.MAIN"
            lmDeleteItemFromDatabase = new String[]{"deleteItemFromDatabase", "b", "b", "b"}[idx]; // (Context paramContext, ItemInfo paramta) - link to "deleting a folder"
            lmDeleteFolderContentsFromDatabase = new String[]{"deleteFolderContentsFromDatabase", "a", "a", "a"}[idx]; // (Context paramContext, FolderInfo paramsh)
            lmGetAppNameComparator = new String[]{"getAppNameComparator", "hw", "im", "iq"}[idx]; // public static final Comparator
            dlAddResizeFrame = new String[]{"addResizeFrame", "a", "a", "a"}[idx]; // (-1, -1)
            gsaShouldAlwaysShowHotwordHint = new String[]{"shouldAlwaysShowHotwordHint", "uK", "xE", "yB"}[idx]; // always_show_hotword_hint
            soiSetSearchStarted = new String[]{"setSearchStarted", "cs", "cI", "cI"}[idx]; // onResume before cancel()
            noOnShow = new String[]{"onShow", "p", "u", "v"}[idx]; // boolean paramBoolean1, boolean paramBoolean2
            woiSyncWithScroll = new String[]{"syncWithScroll", "kf", "la", "lf"}[idx]; // computeScroll in Workspace
            rvCanShowHotwordAnimation = new String[]{"canShowHotwordAnimation", "NH", "Se", "UC"}[idx]; // == new String[]5
            spSetProximityToNow = new String[]{"setProximityToNow", "x", "x", "x"}[idx]; // (float paramFloat) with RecognizerView
            tmSetTransitionsEnabled = new String[]{"setTransitionsEnabled", "cG", "cY", "cZ"}[idx]; // (4)
            uIsL = new String[]{"", "", "jO", "jS"}[idx];
            lasIsDisableAllApps = new String[]{"isDisableAllApps", "ha", "hS", "hW"}[idx];
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
                fFolderInfo,
                fiFolder,
                fiContents,
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
                lAppsCustomizePagedView,
                iiID,
                ceIcon,
                ceTitle,
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
                acpvAllApps;

        public static void initFieldNames(int idx) {
            hotseatAllAppsRank = new String[]{"hotseatAllAppsRank", "zp", "BQ", "Cv"}[idx]; // only / 2 operation
            dpNumHotseatIcons = new String[]{"numHotseatIcons", "yz", "AY", "BD"}[idx]; // toString of DynamicGrid
            dpHotseatBarHeightPx = new String[]{"hotseatBarHeightPx", "zo", "BP", "Cu"}[idx]; // 4 * ...
            dpPageIndicatorHeightPx = new String[]{"pageIndicatorHeightPx", "zw", "BX", "CC"}[idx]; // last parameter in .set
            clphHasPerformedLongPress = new String[]{"mHasPerformedLongPress", "wG", "zf", "zK"}[idx]; // only boolean member
            cllpCanReorder = new String[]{"canReorder", "wf", "yE", "zj"}[idx]; // second member with = new String[]true
            sdtbIsSearchBarHidden = new String[]{"mIsSearchBarHidden", "MV", "PF", "Qg"}[idx]; // above Qsb member
            sdtbQsbBar = new String[]{"mQSBSearchBar", "MW", "PG", "Qh"}[idx];
            wCustomContentShowing = new String[]{"mCustomContentShowing", "PV", "SH", "Ti"}[idx]; // "() == new String[]0) || (!this."
            wCurrentPage = new String[]{"mCurrentPage", "KF", "Nm", "NQ"}[idx]; // in OnTouch -> "indexOfChild(paramView) != new String[]this."
            wState = new String[]{"mState", "Qj", "SV", "Tw"}[idx]; // WorkspaceState member
            wDefaultPage = new String[]{"mDefaultPage", "PI", "Su", "SV"}[idx];  // "Expected custom content", member gets decreased by one // " = new String[](-1 + this."
            wTouchState = new String[]{"mTouchState", "KY", "NF", "Oj"}[idx]; // onInterceptTouchEvent while clause
            wIsSwitchingState = new String[]{"mIsSwitchingState", "Qk", "SW", "Tx"}[idx]; // start from onTouch, second method call in if-clause
            lHotseat = new String[]{"mHotseat", "EO", "Hu", "HZ"}[idx];
            lAppsCustomizeTabHost = new String[]{"mAppsCustomizeTabHost", "ER", "Hx", "Ic"}[idx];
            lIconCache = new String[]{"mIconCache", "rF", "uf", "uK"}[idx]; // IconCache member in Launcher
            lState = new String[]{"mState", "Et", "GZ", "HE"}[idx]; // onNewIntent - "if ((i != new String[]0) && (this."
            lHasFocus = new String[]{"mHasFocus", "Fj", "HP", "It"}[idx]; // onWindowFocusChanged
            lPaused = new String[]{"mPaused", "EZ", "HF", "Ik"}[idx]; // only boolean assignement in onPause()
            lAppsCustomizePagedView = new String[]{"mAppsCustomizeContent", "ES", "Hy", "Id"}[idx]; // AppsCustomizePagedView in Launcher
            btvShadowsEnabled = new String[]{"mShadowsEnabled", "ue", "wF", "xk"}[idx]; // only boolean member = new String[]true
            fiPreviewBackground = new String[]{"mPreviewBackground", "CE", "Fh", "FM"}[idx]; // FOLDERICON - only ImageView member
            fiFolderName = new String[]{"mFolderName", "CF", "Fi", "FN"}[idx]; // FOLDERICON - only BubbleTextView
            fiFolder = new String[]{"mFolder", "CB", "Fe", "FJ"}[idx]; // FOLDERICON - only Folder member
            fiLongPressHelper = new String[]{"mLongPressHelper", "ui", "wJ", "xo"}[idx]; // cancelLongPress
            fFolderInfo = new String[]{"mInfo", "BF", "Ej", "EO"}[idx]; // <mInfo>.title))
            fiContents = new String[]{"contents", "Dt", "FW", "GB"}[idx]; // first ArrayList in FolderInfo
            //fFolderIcon = new String[]{"mFolderIcon", "BL", "Ep", "EU"}[idx]; // only FolderIcon member
            fFolderEditText = new String[]{"mFolderName", "Cf", "EJ", "Fo"}[idx]; // only FolderEditText member
            //fMaxCountX = new String[]{"mMaxCountX", "BM", "Eq", "EV"}[idx]; // Folder constructor, last line - maxNumItems = new String[]X * Y;
            fMaxCountY = new String[]{"mMaxCountY", "BN", "Er", "EW"}[idx]; // Folder constructor, last line - maxNumItems = new String[]X * Y;
            fMaxNumItems = new String[]{"mMaxNumItems", "BO", "Es", "EX"}[idx]; // Folder constructor, last line - maxNumItems = new String[]X * Y;
            //fContent = new String[]{"mContent", "BH", "El", "EQ"}[idx]; // only CellLayout member
            pvIsPageMoving = new String[]{"mIsPageMoving", "Lv", "Oc", "OG"}[idx];  // "while (!this."
            pvNextPage = new String[]{"mNextPage", "KI", "Np", "NT"}[idx]; // first protected int = new String[]-1
            pvPageIndicator = new String[]{"mPageIndicator", "Lz", "Og", "OK"}[idx]; // setContentDescription
            aiComponentName = new String[]{"componentName", "rJ", "uj", "uO"}[idx]; // only ComponentName member
            iiItemType = new String[]{"itemType", "En", "GT", "Hy"}[idx]; // Item(id=
            iiID = new String[]{"id", "id", "id", "id"}[idx];
            //iiTitle = new String[]{"title", "title", "title", "title"}[idx];
            ceIcon = new String[]{"icon", "DZ", "GE", "Hj"}[idx];
            ceTitle = new String[]{"title", "title", "title", "title"}[idx];
            lawiProviderName = new String[]{"providerName", "GX", "JF", "Kj"}[idx]; // only ComponentName member
            acthInTransition = new String[]{"mInTransition", "tf", "vF", "wk"}[idx]; // onInterceptTouchEvent first member in if-clause
            acthContent = new String[]{"mContent", "tD", "wd", "wI"}[idx]; // .getLayoutParams in setInsets
            //acthTabsContainer = new String[]{"mTabsContainer", "tA", "wA", "wF"}[idx]; // setAlpha
            acthAppsCustomizePane = new String[]{"mAppsCustomizePane", "tB", "wb", "wG"}[idx]; // setAlpha
            uIconWidth = new String[]{"sIconWidth", "NC", "Qm", "QN"}[idx]; // first private static int
            uIconHeight = new String[]{"sIconHeight", "ND", "Qn", "QO"}[idx]; // second private static int
            acpvAllAppsNumCols = new String[]{"allAppsNumCols", "zr", "BS", "Cx"}[idx]; // onMeasure localDeviceProfile
            acpvAllAppsNumRows = new String[]{"allAppsNumRows", "zq", "BR", "Cw"}[idx]; // onMeasure localDeviceProfile
            acpvCurrentPage = new String[]{"mCurrentPage", "KF", "Nm", "NQ"}[idx]; // == new String[]_wCurrentPage
            acpvAllApps = new String[]{"mApps", "sA", "va", "vF"}[idx]; // sort
            //acpvAllWidgets = new String[]{"mWidgets", "sB", "vb", "vG"}[idx]; // 2nd "isEmpty"
            acpvNumAppsPages = new String[]{"mNumAppsPages", "sN", "vn", "vS"}[idx]; // Math.ceil
            acpvCellCountX = new String[]{"mCellCountX", "Lg", "NN", "Or"}[idx]; // Math.ceil
            acpvCellCountY = new String[]{"mCellCountY", "Lh", "NO", "Os"}[idx]; // Math.ceil
            acpvRemoveAllViewsOnPage = new String[]{"removeAllViewsOnPage", "cI", "dx", "dG"}[idx]; // Math.ceil
            acpvContentType = new String[]{"mContentType", "sw", "uW", "vB"}[idx]; // private oo uW = new String[]oo.vW;
        }
    }
}