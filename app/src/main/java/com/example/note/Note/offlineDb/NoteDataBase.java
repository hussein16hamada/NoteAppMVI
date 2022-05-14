package com.example.note.Note.offlineDb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.note.Note.models.NoteModel;
import com.example.note.Note.models.Responses.NoteResponseItem;



@Database(entities = {NoteResponseItem.class, NoteModel.class}, version = 1, exportSchema = false)
//@TypeConverters(DateConverter.class)
public abstract class NoteDataBase extends RoomDatabase {

    public abstract NoteDao noteDao();



    private static NoteDataBase myDataBase;
    public static NoteDataBase getInstance(Context context){
        if(myDataBase==null){//Create new object
            myDataBase =
                    Room.databaseBuilder(context.getApplicationContext(),
                            NoteDataBase.class, "Note-DataBase")
                            .fallbackToDestructiveMigration()
                            .build();
        }return myDataBase;
    }

}
