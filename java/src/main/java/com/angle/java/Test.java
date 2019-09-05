package com.angle.java;

public class Test {

    public static void main(String[] args) {
        Name name = new Name(20, "张三");
        System.out.println(name.toString());
        setName(name);
        System.out.println(name.toString());
    }

    public static void setName(Name name) {
        name.setName("李四");
    }

    public static class Name {
        int age;
        String name;

        public Name(int age, String name) {
            this.age = age;
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "Name{" +
                    "age=" + age +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
