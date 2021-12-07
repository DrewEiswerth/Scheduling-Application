package org.SchedulingApplication.Model;

public class ComboBoxItem {

    private final String name;
    private final int id;
    private final boolean displayByID;

    public ComboBoxItem(String name, int id, boolean displayByID) {
        this.name = name;
        this.id = id;
        this.displayByID = displayByID;
    }

    public String getName() {
        return name;
    }
    public int getId() {
        return id;
    }

    @Override  // this method dictates how the object is displayed within any ComboBox
    public String toString() {
        if(!displayByID) {
            return name;
        }
        else {
            return String.valueOf(id);
        }
    }
}
