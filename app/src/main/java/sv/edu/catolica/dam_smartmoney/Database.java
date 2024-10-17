package sv.edu.catolica.dam_smartmoney;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "SmartMoney.db";
    private static int DATABASE_VERSION = 1;

    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USUARIOS = "CREATE TABLE Usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, " +
                "dinero DECIMAL)";
        db.execSQL(CREATE_TABLE_USUARIOS);
        //String CREATE_TABLE_GASTO
        //String CREATE_TABLE_FECHAS
        //String CREATE_TABLE_CATEGORIA
        //String CREATE_TABLE_PAGOS
        //String CREATE_TABLE_HISTORIAL_INGRESOS
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Usuarios");
        onCreate(sqLiteDatabase);
    }
}
