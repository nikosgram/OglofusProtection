package me.nikosgram.oglofus.database;

import java.sql.Connection;

public interface DatabaseDriver
{
    Connection openConnection();

    Boolean checkConnection();

    void closeConnection();

    Connection getConnection();

    String name();
}
