package com.example.practice.ui.contacts;

public class Contact {
    private String name;
    private String phoneNumber;
    private long photoID;
    private long personID;
    private int id;

    public Contact (String name, String phoneNumber, long photoID, long personID) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.photoID = photoID;
        this.personID = personID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPhotoID(long photoID) {
        this.photoID = photoID;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setPersonID(long personID) {
        this.personID = personID;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public long getPersonID() {
        return personID;
    }

    public long getPhotoID() {
        return photoID;
    }

    public int getID() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contact contact = (Contact) o;

        if (!name.equals(contact.name)) return false;
        return phoneNumber.equals(contact.phoneNumber);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + phoneNumber.hashCode();
        return result;
    }
}
