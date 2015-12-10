package com.levor.liferpg.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class Task {
    private String title;
    private List<Skill> relatedSkills = new ArrayList<>();
    private UUID id;
    private int repeatability = -1;

    public static final Comparator<Task> COMPARATOR = new TasksComparator();

    public Task(String title, UUID id, int repeatability, Skill ... skills) {
        this(title, id, repeatability, Arrays.asList(skills));
    }

    public Task(String title, UUID id, int repeatability, List<Skill> skills) {
        this.title = title;
        this.repeatability = repeatability;
        this.relatedSkills = skills;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public List<Skill> getRelatedSkills() {
        Collections.sort(relatedSkills, Skill.LEVEL_COMPARATOR);
        return relatedSkills;
    }

    public String getRelatedSkillsString() {
        Collections.sort(relatedSkills, Skill.LEVEL_COMPARATOR);
        StringBuilder sb = new StringBuilder();
        for (Skill sk : relatedSkills) {
            sb.append(sk.getId())
                    .append("::");
        }
        return sb.toString();
    }

    public void setRelatedSkills(Skill... skills) {
        this.relatedSkills = Arrays.asList(skills);
    }

    public void addRelatedSkill(Skill sk){
        this.relatedSkills.add(sk);
    }

    public void setRelatedSkills(List<Skill> relatedSkills) {
        this.relatedSkills = relatedSkills;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public UUID getId() {
        return id;
    }

    public int getRepeatability() {
        return repeatability;
    }

    public void setRepeatability(int repeatability) {
        this.repeatability = repeatability;
    }

    private static class TasksComparator implements Comparator<Task> {

        @Override
        public int compare(Task lhs, Task rhs) {
            if (lhs.repeatability != rhs.repeatability){
                if (lhs.repeatability == 0) return 1;
                if (rhs.repeatability == 0) return -1;
                if (lhs.repeatability < 0) return -1;
                if (rhs.repeatability < 0) return 1;
                return rhs.repeatability - lhs.repeatability;
            }
            return lhs.getTitle().compareTo(rhs.getTitle());
        }
    }
}
