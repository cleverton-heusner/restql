package io.github.cleverton.heusner.fixture;

import com.google.gson.annotations.SerializedName;

public class Pet {

    private String name;

    @SerializedName("nick_name")
    private String nickName;

    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
