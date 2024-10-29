package sv.edu.catolica.dam_smartmoney;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SmartMoney.db";
    private static final int DATABASE_VERSION = 1;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_USUARIOS = "CREATE TABLE Usuarios " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "dinero DECIMAL NOT NULL)";

        String CREATE_TABLE_PAGOS = "CREATE TABLE Pagos " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "cantidad DECIMAL NOT NULL, " +
                "tipo_pago TEXT NOT NULL)";

        String CREATE_TABLE_FECHAS = "CREATE TABLE Fechas " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "fecha DATE NOT NULL, " +
                "nivel_importancia TEXT)";

        String CREATE_TABLE_CATEGORIA = "CREATE TABLE Categoria " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "categoria_nombre TEXT NOT NULL UNIQUE, " +
                "tipo_gasto TEXT NOT NULL, " +
                "imagen_uri TEXT)";

        String CREATE_TABLE_GASTO = "CREATE TABLE Gasto " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre_gasto TEXT NOT NULL, " +
                "fecha_gasto INTEGER, " +
                "categoria_gasto INTEGER, " +
                "pago_gasto INTEGER, " +
                "FOREIGN KEY (fecha_gasto) REFERENCES Fechas(id), " +
                "FOREIGN KEY (categoria_gasto) REFERENCES Categoria(id), " +
                "FOREIGN KEY (pago_gasto) REFERENCES Pagos(id))";

        String CREATE_TABLE_HISTORIAL_INGRESOS = "CREATE TABLE Historial_Ingresos " +
                "(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario_id INTEGER, " +
                "cantidad DECIMAL NOT NULL, " +
                "fecha_actualizacion DATE NOT NULL, " +
                "FOREIGN KEY (usuario_id) REFERENCES Usuarios(id))";

        // Ejecutar las consultas SQL para crear las tablas
        db.execSQL(CREATE_TABLE_USUARIOS);
        db.execSQL(CREATE_TABLE_PAGOS);
        db.execSQL(CREATE_TABLE_FECHAS);
        db.execSQL(CREATE_TABLE_CATEGORIA);
        db.execSQL(CREATE_TABLE_GASTO);
        db.execSQL(CREATE_TABLE_HISTORIAL_INGRESOS);

        //Default para categoria
        String INSERT_DEFAULT_CATEGORIA = "INSERT INTO Categoria (categoria_nombre, tipo_gasto, imagen_uri) " +
                "VALUES ('Importante', 'Importante', NULL)";
        db.execSQL(INSERT_DEFAULT_CATEGORIA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Usuarios");
        db.execSQL("DROP TABLE IF EXISTS Pagos");
        db.execSQL("DROP TABLE IF EXISTS Fechas");
        db.execSQL("DROP TABLE IF EXISTS Categoria");
        db.execSQL("DROP TABLE IF EXISTS Gasto");
        db.execSQL("DROP TABLE IF EXISTS Historial_Ingresos");
        onCreate(db);
    }
}
