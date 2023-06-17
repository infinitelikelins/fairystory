package com.bearya.robot.fairystory.walk.car;

import com.bearya.robot.base.load.BaseLoad;
import com.bearya.robot.base.load.ILoadMgr;
import com.bearya.robot.base.util.DebugUtil;
import com.bearya.robot.base.walk.LoadEntrance;
import com.bearya.robot.fairystory.ui.res.ThemeConfig;
import com.bearya.robot.fairystory.walk.load.ArmorLoad;
import com.bearya.robot.fairystory.walk.load.BigMonsterLoad;
import com.bearya.robot.fairystory.walk.load.CannibalFlowerLoad;
import com.bearya.robot.fairystory.walk.load.CastleEndLoad;
import com.bearya.robot.fairystory.walk.load.CompassLoad;
import com.bearya.robot.fairystory.walk.load.CrocodileLakeLoad;
import com.bearya.robot.fairystory.walk.load.CrystalShoesLoad;
import com.bearya.robot.fairystory.walk.load.DanceSkirtLoad;
import com.bearya.robot.fairystory.walk.load.DragonEndLoad;
import com.bearya.robot.fairystory.walk.load.EndLoad;
import com.bearya.robot.fairystory.walk.load.EquipmentLoad;
import com.bearya.robot.fairystory.walk.load.FatTonnyLoad;
import com.bearya.robot.fairystory.walk.load.GrasslandLoad;
import com.bearya.robot.fairystory.walk.load.IdeaEndLoad;
import com.bearya.robot.fairystory.walk.load.KeyLoad;
import com.bearya.robot.fairystory.walk.load.MineEndLoad;
import com.bearya.robot.fairystory.walk.load.NineTailedCatLoad;
import com.bearya.robot.fairystory.walk.load.PegasusLoad;
import com.bearya.robot.fairystory.walk.load.SpiderLoad;
import com.bearya.robot.fairystory.walk.load.StartLoad;
import com.bearya.robot.fairystory.walk.load.StationBlueLoad;
import com.bearya.robot.fairystory.walk.load.StationGreenLoad;
import com.bearya.robot.fairystory.walk.load.StationLoad;
import com.bearya.robot.fairystory.walk.load.StationPinkLoad;
import com.bearya.robot.fairystory.walk.load.StationPurpleLoad;
import com.bearya.robot.fairystory.walk.load.StationRedLoad;
import com.bearya.robot.fairystory.walk.load.StationYellowLoad;
import com.bearya.robot.fairystory.walk.load.SwordLoad;
import com.bearya.robot.fairystory.walk.load.TreasureMapLoad;
import com.bearya.robot.fairystory.walk.load.TreeDemonLoad;
import com.bearya.robot.fairystory.walk.load.VolcanicLoad;
import com.bearya.robot.fairystory.walk.load.WitchLoad;
import com.bearya.robot.fairystory.walk.load.ZombieLoad;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoadMgr implements ILoadMgr {

    private final Map<String, BaseLoad> loads = new HashMap<>();
    private final List<LoadEntrance> loadHistory = new ArrayList<>();//走过的路
    private final List<String> equipmentLoads = new ArrayList<>();//设备经过的装备地垫
    private final List<String> lostEquipmentLoads = new ArrayList<>();//到达终点还缺失的地垫
    private EndLoad themeEndLoad;

    /**
     * 当前路况:包括小贝从哪进来从哪出去
     */
    private final LoadEntrance currentLoadEntrance = new LoadEntrance();

    private static LoadMgr instance;

    public static LoadMgr getInstance() {
        if (instance == null) {
            instance = new LoadMgr();
        }
        return instance;
    }

    private LoadMgr() {
        loads.put(StartLoad.NAME, new StartLoad());
        loads.put(GrasslandLoad.NAME, new GrasslandLoad());
        loads.put(MineEndLoad.NAME, new MineEndLoad());
        loads.put(DragonEndLoad.NAME, new DragonEndLoad());
        loads.put(CastleEndLoad.NAME, new CastleEndLoad());
        loads.put(CompassLoad.NAME, new CompassLoad());
        loads.put(TreasureMapLoad.NAME, new TreasureMapLoad());
        loads.put(KeyLoad.NAME, new KeyLoad());
        loads.put(PegasusLoad.NAME, new PegasusLoad());
        loads.put(ArmorLoad.NAME, new ArmorLoad());
        loads.put(SwordLoad.NAME, new SwordLoad());
        loads.put(FatTonnyLoad.NAME, new FatTonnyLoad());
        loads.put(DanceSkirtLoad.NAME, new DanceSkirtLoad());
        loads.put(VolcanicLoad.NAME, new VolcanicLoad());
        loads.put(WitchLoad.NAME, new WitchLoad());
        loads.put(CrystalShoesLoad.NAME, new CrystalShoesLoad());
        loads.put(CrocodileLakeLoad.NAME, new CrocodileLakeLoad());
        loads.put(ZombieLoad.NAME, new ZombieLoad());
        loads.put(SpiderLoad.NAME, new SpiderLoad());
        loads.put(NineTailedCatLoad.NAME, new NineTailedCatLoad());
        loads.put(BigMonsterLoad.NAME, new BigMonsterLoad());
        loads.put(TreeDemonLoad.NAME, new TreeDemonLoad());
        loads.put(CannibalFlowerLoad.NAME, new CannibalFlowerLoad());
        loads.put(StationBlueLoad.NAME, new StationBlueLoad());
        loads.put(StationGreenLoad.NAME, new StationGreenLoad());
        loads.put(StationPinkLoad.NAME, new StationPinkLoad());
        loads.put(StationPurpleLoad.NAME, new StationPurpleLoad());
        loads.put(StationRedLoad.NAME, new StationRedLoad());
        loads.put(StationYellowLoad.NAME, new StationYellowLoad());
        loads.put(IdeaEndLoad.NAME, new IdeaEndLoad());
    }

    public EndLoad getThemeEndLoad() {
        return themeEndLoad;
    }

    public List<String> getLostEquipmentLoads() {
        return lostEquipmentLoads;
    }

    public void setThemeEndLoad(String type) {
        if (ThemeConfig.THEME_MHWH.equals(type)) { // 梦幻舞会
            themeEndLoad = (EndLoad) getLoad(CastleEndLoad.NAME);
        } else if (ThemeConfig.THEME_QHXB.equals(type)) { // 奇幻寻宝
            themeEndLoad = (EndLoad) getLoad(MineEndLoad.NAME);
        } else if (ThemeConfig.THEME_YXWH.equals(type)) {// 英雄无敌
            themeEndLoad = (EndLoad) getLoad(DragonEndLoad.NAME);
        } else if (ThemeConfig.THEME_CXTD.equals(type)) {// 创想天地
            themeEndLoad = (EndLoad) getLoad(IdeaEndLoad.NAME);
        }
    }

    public boolean inLoad(int oid) {
        return getLoad(oid) != null;
    }

    public BaseLoad getLoad(int oid) {
        Collection<BaseLoad> loadCollection = loads.values();
        for (BaseLoad load : loadCollection) {
            if (load.getOidSection().in(oid)) {
                return load;
            }
        }
        return null;
    }

    public BaseLoad getLoad(String name) {
        if (loads.containsKey(name)) {
            return loads.get(name);
        }
        return null;
    }

    public void clear() {
        loadHistory.clear();
        currentLoadEntrance.reset();
        equipmentLoads.clear();
    }

    public void release() {
        clear();
        loads.clear();
    }

    public void addHistory(LoadEntrance newInstance) {
        if (newInstance.getLoad() instanceof EquipmentLoad) {
            EquipmentLoad equipmentLoad = (EquipmentLoad) newInstance.getLoad();
            equipmentLoads.add(equipmentLoad.getName());
        }
        DebugUtil.debug("添加路径记录:%s - %s", newInstance.getLoadName(), newInstance.getLoad().getClass().getSimpleName());
        loadHistory.add(newInstance);
    }

    public LoadEntrance getCurrentLoadEntrance() {
        return currentLoadEntrance;
    }

    public List<String> getLostEquipmentLoadList(EndLoad endLoad) {
        lostEquipmentLoads.clear();
        if (endLoad != themeEndLoad) {
            return null;
        }
        String[] eqLoads = themeEndLoad.getEquipmentLoads();
        if (eqLoads.length > 0) {
            for (String eName : eqLoads) {
                if (!equipmentLoads.contains(eName)) {
                    lostEquipmentLoads.add(eName);
                }
            }
        }
        return lostEquipmentLoads;
    }

    public int getStationLoadNumber() {
        int number = 0;
        for (LoadEntrance loadEntrance : loadHistory) {
            if (loadEntrance.getLoad() instanceof StationLoad) {
                number++;
            }
        }
        return number;
    }

}