package sv.edu.catolica.dam_smartmoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Date;

public class DatabaseHelper {
    private final Context context;

    public DatabaseHelper(Context context) {
        this.context = context;
    }

    //Consulter al inicio si existe ya el usuario
    public boolean verificarnuevousuario() {
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();
        // Consulta para contar el número de usuarios
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Usuarios", null);
        boolean resultado = false;

        if (cursor.moveToFirst()) {
            int count = cursor.getInt(0);
            // Retorna true si solo hay un usuario en la tabla
            resultado = count == 1;
        }

        cursor.close();
        db.close();
        return resultado;
    }

    //Create user para cuando ingresa por primera vez
    public boolean crear_usuario(String nombre, Double monto){
        Database database = new Database(context);
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues registrar = new ContentValues();
        registrar.put("nombre", nombre);
        registrar.put("dinero", monto);

        long resultado = db.insert( "Usuarios", null, registrar);
        db.close();

        return resultado != -1;
    }

    //Obtener el saldo del ultimo usuario para evitar fallos
    public double get_saldo(){
        Database database = new Database(context);
        SQLiteDatabase db = database.getWritableDatabase();
        Double dinero = null;

        Cursor cursor = db.rawQuery("SELECT dinero FROM Usuarios WHERE dinero IS NOT NULL ORDER BY id DESC LIMIT 1", null);
        if (cursor.moveToFirst()){
            dinero = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return dinero;
    }

    //Insertar categorias
    public boolean crear_categorias(String categoria_nombre, String tipo_gasto, String uri){
        Database database = new Database(context);
        SQLiteDatabase db = database.getWritableDatabase();

        ContentValues categoria = new ContentValues();
        categoria.put("categoria_nombre", categoria_nombre);
        categoria.put("tipo_gasto", tipo_gasto);
        categoria.put("imagen_uri", uri);  // Puede ser null si el usuario no seleccionó una imagen

        long resultado_ingreso = db.insert("Categoria", null, categoria);
        db.close();

        return resultado_ingreso != -1;
    }

    //Insertar datos para la tabla gastos, Fechas, Categoria (Solo obtiene su id) pagos
    public void crear_gasto(Date fecha, String nombre_gasto, Double cantidad, String tipo) {
        Database database = new Database(context);
        SQLiteDatabase db = database.getWritableDatabase();

        // Formato para la fecha
        ContentValues datos_fecha = new ContentValues();
        datos_fecha.put("fecha", new java.text.SimpleDateFormat("yyyy-MM-dd").format(fecha));
        datos_fecha.put("nivel_importancia", "Alta"); // Ajusta según tu lógica
        long resultado_fecha = db.insert("Fechas", null, datos_fecha);

        // Obtener el id de la categoría "Importante"
        Cursor categoria_importante = db.rawQuery("SELECT id FROM Categoria WHERE categoria_nombre = 'Importante'", null);
        int id_categoria = -1;
        if (categoria_importante.moveToFirst()) {
            id_categoria = categoria_importante.getInt(0);
        }
        categoria_importante.close();

        if (id_categoria == -1) {
            // Manejar el caso donde no se encuentra la categoría (puedes lanzar un error o usar un valor predeterminado)
            db.close();
            throw new RuntimeException("Categoría 'Importante' no encontrada en la tabla Categoria.");
        }

        // Insertar datos en la tabla Pagos
        ContentValues datos_pagos = new ContentValues();
        datos_pagos.put("cantidad", cantidad);
        datos_pagos.put("tipo_pago", tipo);
        long resultado_pagos = db.insert("Pagos", null, datos_pagos);

        // Insertar en la tabla Gasto con las referencias obtenidas
        ContentValues datos_gasto = new ContentValues();
        datos_gasto.put("nombre_gasto", nombre_gasto);
        datos_gasto.put("fecha_gasto", resultado_fecha);
        datos_gasto.put("categoria_gasto", id_categoria);
        datos_gasto.put("pago_gasto", resultado_pagos);
        db.insert("Gasto", null, datos_gasto);

        db.close();
    }

}