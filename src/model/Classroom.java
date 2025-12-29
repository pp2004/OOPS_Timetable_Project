package model;

public class Classroom {
    private String id;
    private int capacity;
    private boolean audioVideoRequired;
    private int numOfComputers;
    private boolean isLab;

    public Classroom(String id, int capacity, boolean audioVideoRequired, int numOfComputers, boolean isLab) {
        this.id = id;
        this.capacity = capacity;
        this.audioVideoRequired = audioVideoRequired;
        this.numOfComputers = numOfComputers;
        this.isLab = isLab;
    }

    public String getId() { return id; }
    public int getCapacity() { return capacity; }
    public boolean isAudioVideoRequired() { return audioVideoRequired; }
    public int getNumOfComputers() { return numOfComputers; }
    public boolean isLab() { return isLab; }

    // added this part 
public void setCapacity(int capacity) { this.capacity = capacity; }
public void setAudioVideoRequired(boolean audioVideoRequired) { this.audioVideoRequired = audioVideoRequired; }
public void setNumOfComputers(int numOfComputers) { this.numOfComputers = numOfComputers; }
public void setLab(boolean lab) { isLab = lab; }

    public boolean isSuitableFor(int studentCount, boolean needsLab, boolean needsAV, int requiredComputers) {
        if (this.capacity < studentCount) return false;
        if (needsLab && !this.isLab) return false;
        if (needsAV && !this.audioVideoRequired) return false;
        if (requiredComputers > this.numOfComputers) return false;
        return true;
    }

    @Override
    public String toString() {
        return id + " (Cap: " + capacity + ", AV: " + audioVideoRequired + ", Comp: " + numOfComputers + ", Lab: " + isLab + ")";
    }
}
