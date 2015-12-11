package com.levor.liferpg.Controller;

import android.content.Context;

import com.levor.liferpg.Model.Characteristic;
import com.levor.liferpg.Model.Hero;
import com.levor.liferpg.Model.LifeEntity;
import com.levor.liferpg.Model.Skill;
import com.levor.liferpg.Model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LifeController {
    private LifeEntity lifeEntity;

    private static LifeController LifeController;
    public static LifeController getInstance(Context context){
        if (LifeController == null){
            LifeController = new LifeController(context);
        }
        return LifeController;
    }

    private LifeController(Context context) {
        lifeEntity = LifeEntity.getInstance(context);
    }

    public List<Task> getAllTasks(){
        return lifeEntity.getTasks();
    }

    public Map<String, Integer[]> getSkillsTitlesAndLevels(){
        return lifeEntity.getSkillsTitlesAndLevels();
    }

    public List<Skill> getAllSkills(){
        return lifeEntity.getSkills();
    }

    public Task getTaskByTitle(String s) {
        return lifeEntity.getTaskByTitle(s);
    }

    public void createNewTask(String title, int repeatability, List<String> relatedSkills) {
        lifeEntity.addTask(title,repeatability, relatedSkills);
    }


    public void updateTask(Task task) {
        lifeEntity.updateTask(task);
    }

    public void addSkill(String title, Characteristic keyChar){
        lifeEntity.addSkill(title, keyChar);
    }

    public Skill getSkillByTitle(String title) {
        return lifeEntity.getSkillByTitle(title);
    }

    public List<Task> getTasksBySkill(Skill sk){
        return lifeEntity.getTasksBySkill(sk);
    }

    public void removeTask(Task task) {
        lifeEntity.removeTask(task);
    }

    public String[] getCharacteristicsTitleAndLevelAsArray(){
        List<Characteristic> characteristics = lifeEntity.getCharacteristics();
        ArrayList<String> strings = new ArrayList<>();
        for (Characteristic ch : characteristics){
            strings.add(ch.getTitle() + " - " + ch.getLevel());
        }
        return strings.toArray(new String[strings.size()]);
    }

    public String[] getCharacteristicsTitlesArray(){
        List<Characteristic> characteristics = lifeEntity.getCharacteristics();
        ArrayList<String> strings = new ArrayList<>();
        for (Characteristic ch : characteristics){
            strings.add(ch.getTitle());
        }
        return strings.toArray(new String[strings.size()]);
    }

    public Characteristic getCharacteristicByTitle(String title) {
        return lifeEntity.getCharacteristicByTitle(title);
    }

    public ArrayList<Skill> getSkillsByCharacteristic(Characteristic ch) {
        return lifeEntity.getSkillsByCharacteristic(ch);
    }

    public Task getTaskByID(UUID id) {
        return lifeEntity.getTaskByID(id);
    }

    public Skill getSkillByID(UUID id) {
        return lifeEntity.getSkillByID(id);
    }

    public void removeSkill(Skill skill) {
        List<Task> tasks = getTasksBySkill(skill);
        for (Task t : tasks){
            List<Skill> newSkills = new ArrayList<>();
            for (Skill sk : t.getRelatedSkills()){
                if (!sk.equals(skill)){
                    newSkills.add(sk);
                }
            }
            t.setRelatedSkills(newSkills);
        }
        lifeEntity.removeSkill(skill);
    }

    /**
     * Increases or decreases skill sublevel and hero xp.
     * @param sk skill to update
     * @param increase increases values if true, decreases if false.
     * @return true if hero level changed, false otherwise.
     */
    public boolean changeSkillSubLevel(Skill sk, boolean increase){
        //TODO move to LifeEntity
        Hero hero = lifeEntity.getHero();
        boolean result;
        if (increase){
            if (sk.increaseSublevel()){
                lifeEntity.updateCharacteristic(sk.getKeyCharacteristic());
            }
            result = hero.increaseXP();
        } else {
            if (sk.decreaseSublevel()){
                lifeEntity.updateCharacteristic(sk.getKeyCharacteristic());
            }
            result = hero.decreaseXP();
        }
        lifeEntity.updateSkill(sk);
        lifeEntity.updateHero(hero);
        return result;
    }

    public void updateSkill(Skill skill) {
        lifeEntity.updateSkill(skill);
    }

    public Hero getHero(){
        return lifeEntity.getHero();
    }

    public String getHeroName(){
        return lifeEntity.getHero().getName();
    }

    public int getHeroLevel(){
        return lifeEntity.getHero().getLevel();
    }

    public int getHeroXp(){
        return lifeEntity.getHero().getXp();
    }

    public int getHeroXpToNextLevel(){
        return lifeEntity.getHero().getXpToNextLevel();
    }

    public void updateHeroName(String name){
        Hero hero = lifeEntity.getHero();
        hero.setName(name);
        lifeEntity.updateHero(hero);
    }
}
