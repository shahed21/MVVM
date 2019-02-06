package com.shahedrahim.mvvm;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

/**
 * This class defines the Database for this app
 * It includes {@link NoteDao}
 */
@Database(entities = {Note.class}, version=1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {

    //Static instance of the Database
    private static NoteDatabase instance;

    // Handler for the Note Room
    public abstract NoteDao noteDao();

    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class,
                    "note_database")
                    .fallbackToDestructiveMigration() //Destroys the old DB
                    .addCallback(roomCallback)  //Creates tables in the new DB
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private NoteDao noteDao;

        public PopulateDbAsyncTask(NoteDatabase db) {
            this.noteDao = db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Dog", "Animals", 1));
            noteDao.insert(new Note("Cat", "Animals", 2));
            noteDao.insert(new Note("Horse", "Animals", 3));
            return null;
        }
    }
}
