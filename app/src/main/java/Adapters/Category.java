package Adapters;

public class Category {

    String name;
    String img;

    int id;

    public Category(String name, String img) {
        this.name = name;
        this.img = img;
    }

    public Category(String name, String img, int id) {
        this.name = name;
        this.img = img;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
