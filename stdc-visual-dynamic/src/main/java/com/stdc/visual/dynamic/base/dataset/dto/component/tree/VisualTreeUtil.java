package com.stdc.visual.dynamic.base.dataset.dto.component.tree;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stdc.core.tool.utils.ObjectUtil;
import com.stdc.core.tool.utils.StringUtil;

import java.util.*;

/**
 * @author: wang_jie
 * @data: 2022/5/26--17:25
 * @describe: 初始化层级树数据
 */
public class VisualTreeUtil {
    /**
     * 初始化根节点
     * @param treeS
     * @param dataS
     * @return
     */
    public static Map<String, VisualTree> initRootTree(List<Set<VisualTree>> treeS, JSONArray dataS){
        Map<String, VisualTree> rootMap = new HashMap<>();
        switch (treeS.size()){
            case 1:
                rootMap = initTreeForOne(treeS,dataS);
                break;
            case 2:
                rootMap = initTreeForTwo(treeS,dataS);
                break;
            case 3:
                rootMap = initTreeForThree(treeS,dataS);
                break;
            case 4:
                rootMap = initTreeForFour(treeS,dataS);
                break;
            case 5:
                rootMap = initTreeForFive(treeS,dataS);
                break;
            case 6:
                rootMap = initTreeForSix(treeS,dataS);
                break;
            case 7:
                rootMap = initTreeForSeven(treeS,dataS);
                break;
            case 8:
                rootMap = initTreeForEight(treeS,dataS);
                break;
            case 9:
                rootMap = initTreeForNine(treeS,dataS);
                break;
            default:
                break;
        }
        return rootMap;
    }
    private static Map<String, VisualTree> initTreeForOne(List<Set<VisualTree>> treeS, JSONArray dataS){
        //缓存
        List<String[]> lastListS = new LinkedList<>();
        //获取筛选条件
        Set<VisualTree> rootS = treeS.get(0);
        for (VisualTree root : rootS) {
            for (Object d : dataS) {
                JSONObject data = (JSONObject) d;
                Boolean flag = true;
                flag = flag && StringUtil.equals(data.getString(root.getId()),root.getLabel());
                if (flag){
                    String[] filter = {root.getId()+"_&_"+root.getLabel()};
                    lastListS.add(filter);
                }
            }
        }
        Map<String,VisualTree> rootMap = new HashMap<>();
        lastListS.forEach(filter->{
            String[] fields = filter[0].split("_&_");
            VisualTree rooNode = ObjectUtil.isEmpty(rootMap.get(filter[0]))?new VisualTree():rootMap.get(filter[0]);
            rooNode.setId(fields[0]);
            rooNode.setLabel(fields[1]);
            rootMap.put(filter[0],rooNode);
        });
        return rootMap;
    }
    private static Map<String, VisualTree> initTreeForTwo(List<Set<VisualTree>> treeS, JSONArray dataS){
        //缓存
        List<String[]> lastListS = new LinkedList<>();
        //获取筛选条件
        Set<VisualTree> rootS = treeS.get(0);
        Set<VisualTree> secondS = treeS.get(1);
        for (VisualTree root : rootS) {
            for (VisualTree second : secondS) {
                for (Object d : dataS) {
                    JSONObject data = (JSONObject) d;
                    Boolean flag = true;
                    flag = flag && StringUtil.equals(data.getString(root.getId()),root.getLabel());
                    flag = flag && StringUtil.equals(data.getString(second.getId()),second.getLabel());
                    if (flag){
                        String[] filter = {root.getId()+"_&_"+root.getLabel(),second.getId()+"_&_"+second.getLabel()};
                        lastListS.add(filter);
                    }
                }
            }
        }
        Map<String,VisualTree> rootMap = new HashMap<>();
        lastListS.forEach(filter->{
            String[] fields = filter[0].split("_&_");
            VisualTree rooNode = ObjectUtil.isEmpty(rootMap.get(filter[0]))?new VisualTree():rootMap.get(filter[0]);
            rooNode.setId(fields[0]);
            rooNode.setLabel(fields[1]);
            rootMap.put(filter[0],rooNode);

            fields = filter[1].split("_&_");
            VisualTree secondNode =  new VisualTree();
            secondNode.setId(fields[0]);
            secondNode.setLabel(fields[1]);

            rooNode.getChildren().add(secondNode);
        });
        return rootMap;
    }
    private static Map<String, VisualTree> initTreeForThree(List<Set<VisualTree>> treeS, JSONArray dataS){
        //缓存
        List<String[]> lastListS = new LinkedList<>();
        //获取筛选条件
        Set<VisualTree> rootS = treeS.get(0);
        Set<VisualTree> secondS = treeS.get(1);
        Set<VisualTree> lastS = treeS.get(2);
        for (VisualTree root : rootS) {
            for (VisualTree second : secondS) {
                for (VisualTree last : lastS) {
                    for (Object d : dataS) {
                        JSONObject data = (JSONObject) d;
                        Boolean flag = true;
                        flag = flag && StringUtil.equals(data.getString(root.getId()),root.getLabel());
                        flag = flag && StringUtil.equals(data.getString(second.getId()),second.getLabel());
                        flag = flag && StringUtil.equals(data.getString(last.getId()),last.getLabel());
                        if (flag){
                            String[] filter = {root.getId()+"_&_"+root.getLabel(),second.getId()+"_&_"+second.getLabel(),last.getId()+"_&_"+last.getLabel()};
                            lastListS.add(filter);
                        }
                    }
                }
            }
        }
        Map<String,VisualTree> rootMap = new HashMap<>();
        Map<String,VisualTree> otherMap = new HashMap<>();
        lastListS.forEach(filter->{
            String[] fields = filter[0].split("_&_");
            VisualTree rooNode = ObjectUtil.isEmpty(rootMap.get(filter[0]))?new VisualTree():rootMap.get(filter[0]);
            rooNode.setId(fields[0]);
            rooNode.setLabel(fields[1]);
            rootMap.put(filter[0],rooNode);

            fields = filter[1].split("_&_");
            VisualTree secondNode = ObjectUtil.isEmpty(otherMap.get(filter[0] + "__&__" + filter[1]))
                    ? new VisualTree() : otherMap.get(filter[0] + "__&__" + filter[1]);
            secondNode.setId(fields[0]);
            secondNode.setLabel(fields[1]);
            otherMap.put(filter[0] + "__&__" + filter[1],secondNode);

            fields = filter[2].split("_&_");
            VisualTree lastNode = new VisualTree();
            lastNode.setId(fields[0]);
            lastNode.setLabel(fields[1]);

            rooNode.getChildren().add(secondNode);
            secondNode.getChildren().add(lastNode);
        });
        return rootMap;
    }
    private static Map<String, VisualTree> initTreeForFour(List<Set<VisualTree>> treeS, JSONArray dataS){
        //缓存
        List<String[]> lastListS = new LinkedList<>();
        //获取筛选条件
        Set<VisualTree> rootS = treeS.get(0);
        Set<VisualTree> secondS = treeS.get(1);
        Set<VisualTree> threeS = treeS.get(2);
        Set<VisualTree> lastS = treeS.get(3);
        for (VisualTree root : rootS) {
            for (VisualTree second : secondS) {
                for (VisualTree three : threeS) {
                    for (VisualTree last : lastS) {
                        for (Object d : dataS) {
                            JSONObject data = (JSONObject) d;
                            Boolean flag = true;
                            flag = flag && StringUtil.equals(data.getString(root.getId()),root.getLabel());
                            flag = flag && StringUtil.equals(data.getString(second.getId()),second.getLabel());
                            flag = flag && StringUtil.equals(data.getString(three.getId()),three.getLabel());
                            flag = flag && StringUtil.equals(data.getString(last.getId()),last.getLabel());
                            if (flag){
                                String[] filter = {root.getId()+"_&_"+root.getLabel(),second.getId()+"_&_"+second.getLabel(),three.getId()+"_&_"+three.getLabel(),last.getId()+"_&_"+last.getLabel()};
                                lastListS.add(filter);
                            }
                        }
                    }
                }
            }
        }
        Map<String,VisualTree> rootMap = new HashMap<>();
        Map<String,VisualTree> otherMap = new HashMap<>();
        lastListS.forEach(filter->{
            String[] fields = filter[0].split("_&_");
            VisualTree rooNode = ObjectUtil.isEmpty(rootMap.get(filter[0]))?new VisualTree():rootMap.get(filter[0]);
            rooNode.setId(fields[0]);
            rooNode.setLabel(fields[1]);
            rootMap.put(filter[0],rooNode);

            fields = filter[1].split("_&_");
            VisualTree secondNode = ObjectUtil.isEmpty(otherMap.get(filter[0] + "__&__" + filter[1]))
                    ? new VisualTree() : otherMap.get(filter[0] + "__&__" + filter[1]);
            secondNode.setId(fields[0]);
            secondNode.setLabel(fields[1]);
            otherMap.put(filter[0] + "__&__" + filter[1],secondNode);

            fields = filter[2].split("_&_");
            VisualTree threeNode = ObjectUtil.isEmpty(otherMap.get(filter[0] + "__&__" + filter[1]+ "__&__" + filter[2]))
                    ? new VisualTree() : otherMap.get(filter[0] + "__&__" + filter[1]+ "__&__" + filter[2]);
            threeNode.setId(fields[0]);
            threeNode.setLabel(fields[1]);
            otherMap.put(filter[0] + "__&__" + filter[1]+ "__&__" + filter[2],threeNode);

            fields = filter[3].split("_&_");
            VisualTree lastNode = new VisualTree();
            lastNode.setId(fields[0]);
            lastNode.setLabel(fields[1]);

            rooNode.getChildren().add(secondNode);
            secondNode.getChildren().add(threeNode);
            threeNode.getChildren().add(lastNode);
        });
        return rootMap;
    }
    private static Map<String, VisualTree> initTreeForFive(List<Set<VisualTree>> treeS, JSONArray dataS){
        //缓存
        List<String[]> lastListS = new LinkedList<>();
        //获取筛选条件
        Set<VisualTree> rootS = treeS.get(0);
        Set<VisualTree> secondS = treeS.get(1);
        Set<VisualTree> threeS = treeS.get(2);
        Set<VisualTree> fourS = treeS.get(3);
        Set<VisualTree> lastS = treeS.get(4);
        for (VisualTree root : rootS) {
            for (VisualTree second : secondS) {
                for (VisualTree three : threeS) {
                    for (VisualTree four : fourS) {
                        for (VisualTree last : lastS) {
                            for (Object d : dataS) {
                                JSONObject data = (JSONObject) d;
                                Boolean flag = true;
                                flag = flag && StringUtil.equals(data.getString(root.getId()),root.getLabel());
                                flag = flag && StringUtil.equals(data.getString(second.getId()),second.getLabel());
                                flag = flag && StringUtil.equals(data.getString(three.getId()),three.getLabel());
                                flag = flag && StringUtil.equals(data.getString(four.getId()),four.getLabel());
                                flag = flag && StringUtil.equals(data.getString(last.getId()),last.getLabel());
                                if (flag){
                                    String[] filter = {root.getId()+"_&_"+root.getLabel(),second.getId()+"_&_"+second.getLabel(),three.getId()+"_&_"+three.getLabel(),four.getId()+"_&_"+four.getLabel(),last.getId()+"_&_"+last.getLabel()};
                                    lastListS.add(filter);
                                }
                            }
                        }
                    }
                }
            }
        }
        Map<String,VisualTree> rootMap = new HashMap<>();
        Map<String,VisualTree> otherMap = new HashMap<>();
        lastListS.forEach(filter->{
            String[] fields = filter[0].split("_&_");
            VisualTree rooNode = ObjectUtil.isEmpty(rootMap.get(filter[0]))?new VisualTree():rootMap.get(filter[0]);
            rooNode.setId(fields[0]);
            rooNode.setLabel(fields[1]);
            rootMap.put(filter[0],rooNode);

            fields = filter[1].split("_&_");
            VisualTree secondNode = ObjectUtil.isEmpty(otherMap.get(filter[0] + "__&__" + filter[1]))
                    ? new VisualTree() : otherMap.get(filter[0] + "__&__" + filter[1]);
            secondNode.setId(fields[0]);
            secondNode.setLabel(fields[1]);
            otherMap.put(filter[0] + "__&__" + filter[1],secondNode);

            fields = filter[2].split("_&_");
            String id3 = filter[0] + "__&__" + filter[1] + "__&__" + filter[2];
            VisualTree threeNode = ObjectUtil.isEmpty(otherMap.get(id3))
                    ? new VisualTree() : otherMap.get(id3);
            threeNode.setId(fields[0]);
            threeNode.setLabel(fields[1]);
            otherMap.put(id3,threeNode);

            fields = filter[3].split("_&_");
            String id4 =filter[0] + "__&__" + filter[1] + "__&__" + filter[2]+ "__&__" + filter[3];
            VisualTree fourdNode = ObjectUtil.isEmpty(otherMap.get(id4))
                    ? new VisualTree() : otherMap.get(id4);
            fourdNode.setId(fields[0]);
            fourdNode.setLabel(fields[1]);
            otherMap.put(id4,fourdNode);

            fields = filter[4].split("_&_");
            VisualTree lastNode = new VisualTree();
            lastNode.setId(fields[0]);
            lastNode.setLabel(fields[1]);

            rooNode.getChildren().add(secondNode);
            secondNode.getChildren().add(threeNode);
            threeNode.getChildren().add(fourdNode);
            fourdNode.getChildren().add(lastNode);
        });
        return rootMap;
    }

    private static Map<String, VisualTree> initTreeForSix(List<Set<VisualTree>> treeS, JSONArray dataS){
        //缓存
        List<String[]> lastListS = new LinkedList<>();
        //获取筛选条件
        Set<VisualTree> rootS = treeS.get(0);
        Set<VisualTree> secondS = treeS.get(1);
        Set<VisualTree> threeS = treeS.get(2);
        Set<VisualTree> fourS = treeS.get(3);
        Set<VisualTree> fiveS = treeS.get(4);
        Set<VisualTree> lastS = treeS.get(5);
        for (VisualTree root : rootS) {
            for (VisualTree second : secondS) {
                for (VisualTree three : threeS) {
                    for (VisualTree four : fourS) {
                        for (VisualTree five : fiveS) {
                            for (VisualTree last : lastS) {
                                for (Object d : dataS) {
                                    JSONObject data = (JSONObject) d;
                                    Boolean flag = true;
                                    flag = flag && StringUtil.equals(data.getString(root.getId()),root.getLabel());
                                    flag = flag && StringUtil.equals(data.getString(second.getId()),second.getLabel());
                                    flag = flag && StringUtil.equals(data.getString(three.getId()),three.getLabel());
                                    flag = flag && StringUtil.equals(data.getString(four.getId()),four.getLabel());
                                    flag = flag && StringUtil.equals(data.getString(five.getId()),five.getLabel());
                                    flag = flag && StringUtil.equals(data.getString(last.getId()),last.getLabel());
                                    if (flag){
                                        String[] filter = {root.getId()+"_&_"+root.getLabel(),second.getId()+"_&_"+second.getLabel(),three.getId()+"_&_"+three.getLabel(),four.getId()+"_&_"+four.getLabel(),five.getId()+"_&_"+five.getLabel(),last.getId()+"_&_"+last.getLabel()};
                                        lastListS.add(filter);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Map<String,VisualTree> rootMap = new HashMap<>();
        Map<String,VisualTree> otherMap = new HashMap<>();
        lastListS.forEach(filter->{
            String[] fields = filter[0].split("_&_");
            VisualTree rooNode = ObjectUtil.isEmpty(rootMap.get(filter[0]))?new VisualTree():rootMap.get(filter[0]);
            rooNode.setId(fields[0]);
            rooNode.setLabel(fields[1]);
            rootMap.put(filter[0],rooNode);

            fields = filter[1].split("_&_");
            VisualTree secondNode = ObjectUtil.isEmpty(otherMap.get(filter[0] + "__&__" + filter[1]))
                    ? new VisualTree() : otherMap.get(filter[0] + "__&__" + filter[1]);
            secondNode.setId(fields[0]);
            secondNode.setLabel(fields[1]);
            otherMap.put(filter[0] + "__&__" + filter[1],secondNode);

            fields = filter[2].split("_&_");
            String id3 = filter[0] + "__&__" + filter[1]+ "__&__" + filter[2];
            VisualTree threeNode = ObjectUtil.isEmpty(otherMap.get(id3))
                    ? new VisualTree() : otherMap.get(id3);
            threeNode.setId(fields[0]);
            threeNode.setLabel(fields[1]);
            otherMap.put(id3,threeNode);

            fields = filter[3].split("_&_");
            String id4 = filter[0] + "__&__" + filter[1]+ "__&__" + filter[2]+ "__&__" + filter[3];
            VisualTree fourNode = ObjectUtil.isEmpty(otherMap.get(id4))
                    ? new VisualTree() : otherMap.get(id4);
            fourNode.setId(fields[0]);
            fourNode.setLabel(fields[1]);
            otherMap.put(id4,fourNode);

            fields = filter[4].split("_&_");
            String id5 = filter[0] + "__&__" + filter[1]+ "__&__" + filter[2]+ "__&__" + filter[3]+ "__&__" + filter[4];
            VisualTree fiveNode = ObjectUtil.isEmpty(otherMap.get(id5))
                    ? new VisualTree() : otherMap.get(id5);
            fiveNode.setId(fields[0]);
            fiveNode.setLabel(fields[1]);
            otherMap.put(id5,fiveNode);

            fields = filter[5].split("_&_");
            VisualTree lastNode = new VisualTree();
            lastNode.setId(fields[0]);
            lastNode.setLabel(fields[1]);

            rooNode.getChildren().add(secondNode);
            secondNode.getChildren().add(threeNode);
            threeNode.getChildren().add(fourNode);
            fourNode.getChildren().add(fiveNode);
            fiveNode.getChildren().add(lastNode);
        });
        return rootMap;
    }
    private static Map<String, VisualTree> initTreeForSeven(List<Set<VisualTree>> treeS, JSONArray dataS){
        //缓存
        List<String[]> lastListS = new LinkedList<>();
        //获取筛选条件
        Set<VisualTree> rootS = treeS.get(0);
        Set<VisualTree> secondS = treeS.get(1);
        Set<VisualTree> lastS = treeS.get(2);
        for (VisualTree root : rootS) {
            for (VisualTree second : secondS) {
                for (VisualTree last : lastS) {
                    for (Object d : dataS) {
                        JSONObject data = (JSONObject) d;
                        Boolean flag = true;
                        flag = flag && StringUtil.equals(data.getString(root.getId()),root.getLabel());
                        flag = flag && StringUtil.equals(data.getString(second.getId()),second.getLabel());
                        flag = flag && StringUtil.equals(data.getString(last.getId()),last.getLabel());
                        if (flag){
                            String[] filter = {root.getId()+"_&_"+root.getLabel(),second.getId()+"_&_"+second.getLabel(),last.getId()+"_&_"+last.getLabel()};
                            lastListS.add(filter);
                        }
                    }
                }
            }
        }
        Map<String,VisualTree> rootMap = new HashMap<>();
        Map<String,VisualTree> secondMap = new HashMap<>();
        lastListS.forEach(filter->{
            String[] fields = filter[0].split("_&_");
            VisualTree rooNode = ObjectUtil.isEmpty(rootMap.get(filter[0]))?new VisualTree():rootMap.get(filter[0]);
            rooNode.setId(fields[0]);
            rooNode.setLabel(fields[1]);
            rootMap.put(filter[0],rooNode);

            fields = filter[1].split("_&_");
            VisualTree secondNode = ObjectUtil.isEmpty(secondMap.get(filter[0] + "__&__" + filter[1]))
                    ? new VisualTree() : secondMap.get(filter[0] + "__&__" + filter[1]);
            secondNode.setId(fields[0]);
            secondNode.setLabel(fields[1]);
            secondMap.put(filter[0] + "__&__" + filter[1],secondNode);

            fields = filter[2].split("_&_");
            VisualTree lastNode = new VisualTree();
            lastNode.setId(fields[0]);
            lastNode.setLabel(fields[1]);

            rooNode.getChildren().add(secondNode);
            secondNode.getChildren().add(lastNode);
        });
        return rootMap;
    }

    private static Map<String, VisualTree> initTreeForEight(List<Set<VisualTree>> treeS, JSONArray dataS){
        //缓存
        List<String[]> lastListS = new LinkedList<>();
        //获取筛选条件
        Set<VisualTree> rootS = treeS.get(0);
        Set<VisualTree> secondS = treeS.get(1);
        Set<VisualTree> lastS = treeS.get(2);
        for (VisualTree root : rootS) {
            for (VisualTree second : secondS) {
                for (VisualTree last : lastS) {
                    for (Object d : dataS) {
                        JSONObject data = (JSONObject) d;
                        Boolean flag = true;
                        flag = flag && StringUtil.equals(data.getString(root.getId()),root.getLabel());
                        flag = flag && StringUtil.equals(data.getString(second.getId()),second.getLabel());
                        flag = flag && StringUtil.equals(data.getString(last.getId()),last.getLabel());
                        if (flag){
                            String[] filter = {root.getId()+"_&_"+root.getLabel(),second.getId()+"_&_"+second.getLabel(),last.getId()+"_&_"+last.getLabel()};
                            lastListS.add(filter);
                        }
                    }
                }
            }
        }
        Map<String,VisualTree> rootMap = new HashMap<>();
        Map<String,VisualTree> secondMap = new HashMap<>();
        lastListS.forEach(filter->{
            String[] fields = filter[0].split("_&_");
            VisualTree rooNode = ObjectUtil.isEmpty(rootMap.get(filter[0]))?new VisualTree():rootMap.get(filter[0]);
            rooNode.setId(fields[0]);
            rooNode.setLabel(fields[1]);
            rootMap.put(filter[0],rooNode);

            fields = filter[1].split("_&_");
            VisualTree secondNode = ObjectUtil.isEmpty(secondMap.get(filter[0] + "__&__" + filter[1]))
                    ? new VisualTree() : secondMap.get(filter[0] + "__&__" + filter[1]);
            secondNode.setId(fields[0]);
            secondNode.setLabel(fields[1]);
            secondMap.put(filter[0] + "__&__" + filter[1],secondNode);

            fields = filter[2].split("_&_");
            VisualTree lastNode = new VisualTree();
            lastNode.setId(fields[0]);
            lastNode.setLabel(fields[1]);

            rooNode.getChildren().add(secondNode);
            secondNode.getChildren().add(lastNode);
        });
        return rootMap;
    }

    private static Map<String, VisualTree> initTreeForNine(List<Set<VisualTree>> treeS, JSONArray dataS){
        //缓存
        List<String[]> lastListS = new LinkedList<>();
        //获取筛选条件
        Set<VisualTree> rootS = treeS.get(0);
        Set<VisualTree> secondS = treeS.get(1);
        Set<VisualTree> lastS = treeS.get(2);
        for (VisualTree root : rootS) {
            for (VisualTree second : secondS) {
                for (VisualTree last : lastS) {
                    for (Object d : dataS) {
                        JSONObject data = (JSONObject) d;
                        Boolean flag = true;
                        flag = flag && StringUtil.equals(data.getString(root.getId()),root.getLabel());
                        flag = flag && StringUtil.equals(data.getString(second.getId()),second.getLabel());
                        flag = flag && StringUtil.equals(data.getString(last.getId()),last.getLabel());
                        if (flag){
                            String[] filter = {root.getId()+"_&_"+root.getLabel(),second.getId()+"_&_"+second.getLabel(),last.getId()+"_&_"+last.getLabel()};
                            lastListS.add(filter);
                        }
                    }
                }
            }
        }
        Map<String,VisualTree> rootMap = new HashMap<>();
        Map<String,VisualTree> secondMap = new HashMap<>();
        lastListS.forEach(filter->{
            String[] fields = filter[0].split("_&_");
            VisualTree rooNode = ObjectUtil.isEmpty(rootMap.get(filter[0]))?new VisualTree():rootMap.get(filter[0]);
            rooNode.setId(fields[0]);
            rooNode.setLabel(fields[1]);
            rootMap.put(filter[0],rooNode);

            fields = filter[1].split("_&_");
            VisualTree secondNode = ObjectUtil.isEmpty(secondMap.get(filter[0] + "__&__" + filter[1]))
                    ? new VisualTree() : secondMap.get(filter[0] + "__&__" + filter[1]);
            secondNode.setId(fields[0]);
            secondNode.setLabel(fields[1]);
            secondMap.put(filter[0] + "__&__" + filter[1],secondNode);

            fields = filter[2].split("_&_");
            VisualTree lastNode = new VisualTree();
            lastNode.setId(fields[0]);
            lastNode.setLabel(fields[1]);

            rooNode.getChildren().add(secondNode);
            secondNode.getChildren().add(lastNode);
        });
        return rootMap;
    }

}
