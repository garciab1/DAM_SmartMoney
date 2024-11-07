package sv.edu.catolica.dam_smartmoney;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sv.edu.catolica.dam_smartmoney.Classes.Categoria;
import sv.edu.catolica.dam_smartmoney.Classes.Gasto;

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
    public double get_saldo() {
        Database database = new Database(context);
        SQLiteDatabase db = database.getWritableDatabase();
        double saldo = 0.0;

        try {
            // Obtener el último valor de dinero en la tabla Usuarios
            Cursor cursorDinero = db.rawQuery("SELECT dinero FROM Usuarios WHERE dinero IS NOT NULL ORDER BY id DESC LIMIT 1", null);
            if (cursorDinero.moveToFirst()) {
                saldo = cursorDinero.getDouble(0);
            }
            cursorDinero.close();

            // Obtener el total de los gastos en la tabla Pagos
            Cursor cursorGastos = db.rawQuery("SELECT SUM(cantidad) FROM Pagos", null);
            double totalGastos = 0.0;
            if (cursorGastos.moveToFirst()) {
                totalGastos = cursorGastos.getDouble(0);
            }
            cursorGastos.close();

            // Calcular el saldo final descontando los gastos del dinero del usuario
            saldo -= totalGastos;
        } catch (Exception e) {
            Log.e("get_saldo", "Error al obtener el saldo: ", e);
        } finally {
            db.close();
        }

        return saldo;
    }

    //Obtener el saldo total sin restar
    public double getsaldototal(){
        Database database = new Database(context);
        SQLiteDatabase db = database.getWritableDatabase();
        double saldo = 0.0;

        try {
            // Obtener el último valor de dinero en la tabla Usuarios
            Cursor cursorDinero = db.rawQuery("SELECT dinero FROM Usuarios WHERE dinero IS NOT NULL ORDER BY id DESC LIMIT 1", null);
            if (cursorDinero.moveToFirst()) {
                saldo = cursorDinero.getDouble(0);
            }
            cursorDinero.close();
        } catch (Exception e) {
            Log.e("get_saldo", "Error al obtener el saldo: ", e);
        } finally {
            db.close();
        }

        return saldo;
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

    //Para el metodo de abajo obtener la categorias
    public List<String> getCategorias(){
        List<String> categorias = new ArrayList<>();
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT categoria_nombre FROM Categoria WHERE categoria_nombre != ?", new String[] {"Importante"});
        if (cursor.moveToFirst()){
            do{
                categorias.add(cursor.getString(0));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return categorias;
    }

    //Para el metodo de abajo obtener la categorias con todo y img
    public List<Categoria> getCategoriasIMG() {
        List<Categoria> categorias = new ArrayList<>();
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT categoria_nombre, imagen_uri FROM Categoria", null);

        if (cursor.moveToFirst()) {
            do {
                String CategoriaName = cursor.getString(0);
                String ImagenURI = cursor.getString(1);

                categorias.add(new Categoria(CategoriaName, ImagenURI));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return categorias;
    }

    public boolean crear_gasto(Date fecha, String nombreGasto, Double cantidad, String tipo, String categoria) {
        Database database = new Database(context);
        SQLiteDatabase db = database.getWritableDatabase();
        boolean resultado = false;

        try {
            // Insertar la fecha en la tabla Fechas
            ContentValues datosFecha = new ContentValues();
            datosFecha.put("fecha", new java.text.SimpleDateFormat("yyyy-MM-dd").format(fecha));
            datosFecha.put("nivel_importancia", "Alta"); // Ajusta si necesitas otra lógica
            long idFecha = db.insert("Fechas", null, datosFecha);
            if (idFecha == -1) throw new RuntimeException("Error al insertar fecha en la tabla Fechas.");

            // Obtener el ID de la categoría
            int idCategoria = obtenerIdCategoria(db, categoria != null ? categoria : "Importante");
            if (idCategoria == -1) throw new RuntimeException("Categoría no encontrada en la tabla Categoria.");

            // Insertar datos en la tabla Pagos
            ContentValues datosPagos = new ContentValues();
            datosPagos.put("cantidad", cantidad);
            datosPagos.put("tipo_pago", tipo);
            long idPago = db.insert("Pagos", null, datosPagos);
            if (idPago == -1) throw new RuntimeException("Error al insertar pago en la tabla Pagos.");

            // Insertar datos en la tabla Gasto
            ContentValues datosGasto = new ContentValues();
            datosGasto.put("nombre_gasto", nombreGasto);
            datosGasto.put("fecha_gasto", idFecha);
            datosGasto.put("categoria_gasto", idCategoria);
            datosGasto.put("pago_gasto", idPago);
            long resultadoGasto = db.insert("Gasto", null, datosGasto);

            resultado = resultadoGasto != -1;
        } catch (Exception e) {
            Log.e("crear_gasto", "Error al crear el gasto: ", e);
        } finally {
            db.close();
        }
        return resultado;
    }

    private int obtenerIdCategoria(SQLiteDatabase db, String categoriaNombre) {
        int idCategoria = -1;
        Cursor cursor = db.rawQuery("SELECT id FROM Categoria WHERE categoria_nombre = ?", new String[]{categoriaNombre});
        if (cursor.moveToFirst()) {
            idCategoria = cursor.getInt(0);
        }
        cursor.close();
        return idCategoria;
    }

    public List<String> get_pagos_importantes(){
        List<String> pagos_importantes = new ArrayList<>();
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT Gasto.nombre_gasto, Pagos.cantidad FROM Gasto JOIN Pagos ON Pagos.id == Gasto.pago_gasto ", null);
        if (cursor.moveToFirst()){
            do{
                String nombreGasto = cursor.getString(0);
                double cantidad = cursor.getDouble(1);
                pagos_importantes.add("Gasto: " + nombreGasto + ", Cantidad: " + cantidad);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return pagos_importantes;
    }

    //Metodos para ver los datos en la grafica
    public float GetTotalGasto(){
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();

        // Obtiene el dinero total del usuario más reciente
        Cursor cursor = db.rawQuery("SELECT dinero FROM Usuarios ORDER BY id DESC LIMIT 1", null);
        float totalGasto = 0;
        if (cursor.moveToFirst()) {
            totalGasto = cursor.getFloat(0);
        }
        cursor.close();
        return totalGasto;
    }

    //Mapeo de los gastos segun las categorias
    public Map<String, Float> getGastoPorCategoria(){
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();

        Map<String, Float> gastosPorCategoria = new HashMap<>();
        Cursor cursor = db.rawQuery("SELECT Categoria.categoria_nombre, SUM(Pagos.cantidad) " +
                "FROM Gasto " +
                "JOIN Categoria ON Gasto.categoria_gasto = Categoria.id " +
                "JOIN Pagos ON Gasto.pago_gasto = Pagos.id " +
                "GROUP BY Categoria.categoria_nombre", null);

        while (cursor.moveToNext()) {
            String categoria = cursor.getString(0);
            float cantidad = cursor.getFloat(1);
            gastosPorCategoria.put(categoria, cantidad);
        }
        cursor.close();
        return gastosPorCategoria;
    }

    //Obtener los datos de los gastos con catgoria para mostrarlos en expences
    public List<String> getCategoriaExpences(){
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();

        List<String> datos = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT Gasto.nombre_gasto, Fechas.fecha, Categoria.categoria_nombre FROM Gasto " +
                "JOIN Categoria ON Categoria.id = Gasto.categoria_gasto " +
                "JOIN Fechas ON Fechas.id = Gasto.fecha_gasto", null);

        while (cursor.moveToNext()){
            String Gasto = cursor.getString(0);
            String Fecha = cursor.getString(1);
            String Categoria = cursor.getString(2);
            datos.add("Gasto: " + Gasto + "\n" + "Fecha: " + Fecha + "\n" + "Categoria: " + Categoria);
        }

        return datos;
    }

    //Ver el detalle del calendario
    public String ObtenerEventoFecha(String fechaSeleccionada) {
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();
        StringBuilder gastoDetalles = new StringBuilder();

        String query = "SELECT Gasto.nombre_gasto, Pagos.cantidad, Fechas.fecha FROM Fechas " +
                "JOIN Gasto ON Gasto.fecha_gasto = Fechas.id " +
                "JOIN Pagos ON Pagos.id = Gasto.pago_gasto " +
                "WHERE Fechas.fecha = ?";

        Cursor cursor = db.rawQuery(query, new String[]{fechaSeleccionada});

        // Verificar si hay resultados
        if (cursor.moveToFirst()) {
            do {
                // Obtener los valores de cada columna
                String nombreGasto = cursor.getString(cursor.getColumnIndexOrThrow("nombre_gasto"));
                String cantidad = cursor.getString(cursor.getColumnIndexOrThrow("cantidad"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));

                // Construir una cadena con los detalles
                gastoDetalles.append("Nombre del gasto: ").append(nombreGasto).append("\n");
                gastoDetalles.append("Cantidad: ").append(cantidad).append("\n");
                gastoDetalles.append("Fecha: ").append(fecha).append("\n\n");
            } while (cursor.moveToNext());
        } else {
            cursor.close();
            return null;
        }

        cursor.close();
        return gastoDetalles.toString();
    }

    public boolean UpdateSalario(Double salarioNuevo){
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put("dinero", salarioNuevo);

        int rowsUpdated = db.update("Usuarios", values, null, null); // Sin condición para actualizar todos
        db.close();

        return rowsUpdated > 0;
    }

    //Eliminar gasto
    public boolean eliminarGasto(int gastoId) {
        Database database = new Database(context);
        SQLiteDatabase db = database.getWritableDatabase();
        boolean success = false;

        try{
            db.beginTransaction();
            Cursor cursor = db.rawQuery("SELECT pago_gasto, fecha_gasto FROM Gasto WHERE id = ?", new String[]{String.valueOf(gastoId)});
            if (cursor.moveToFirst()){
                int pagoId = cursor.getInt(cursor.getColumnIndexOrThrow("pago_gasto"));
                int fechaId = cursor.getInt(cursor.getColumnIndexOrThrow("fecha_gasto"));

                int rowsDeletedGasto = db.delete("Gasto", "id = ?", new String[]{String.valueOf(gastoId)});

                // Verifico si se elimino correctamente el gasto
                if (rowsDeletedGasto > 0) {
                    //Eliminar el pago y la fecha que le habia asociado a ese gasto (la categoria no tengo que eliminarla)
                    int rowsDeletedPago = db.delete("Pagos", "id = ?", new String[]{String.valueOf(pagoId)});
                    int rowsDeletedFecha = db.delete("Fechas", "id = ?", new String[]{String.valueOf(fechaId)});

                    success = true; //Si sale bien llega aqui xd
                }
            }
            cursor.close();
            db.setTransactionSuccessful();

        } catch (Exception e) {
            Log.i("Error", e.toString());
        } finally {
            if (db.inTransaction()) {
                db.endTransaction(); // Terminar la transacción si está activa
            }
            db.close();
        }

        return success;
    }

    public List<Gasto> obtenerGastos() {
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();

        List<Gasto> listaGastos = new ArrayList<>();
        // Define the query to get all columns from the Gasto table
        String query = "SELECT id, nombre_gasto, fecha_gasto, categoria_gasto, pago_gasto FROM Gasto";
        Cursor cursor = db.rawQuery(query, null);

        // Loop through each row in the result and create a Gasto object for each
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nombreGasto = cursor.getString(cursor.getColumnIndexOrThrow("nombre_gasto"));
                double cantidad = getCantidadFromPago(cursor.getInt(cursor.getColumnIndexOrThrow("pago_gasto"))); // retrieve from Pagos table
                int fechaGasto = cursor.getInt(cursor.getColumnIndexOrThrow("fecha_gasto"));
                int categoriaGasto = cursor.getInt(cursor.getColumnIndexOrThrow("categoria_gasto"));
                int pagoGasto = cursor.getInt(cursor.getColumnIndexOrThrow("pago_gasto"));

                // Create a Gasto object and add it to the list
                Gasto gasto = new Gasto(id, nombreGasto, cantidad, fechaGasto, categoriaGasto, pagoGasto);
                listaGastos.add(gasto);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return listaGastos;
    }

    // Helper method to get the "cantidad" from Pagos based on pago_gasto ID
    private double getCantidadFromPago(int pagoGastoId) {
        Database database = new Database(context);
        SQLiteDatabase db = database.getReadableDatabase();
        double cantidad = 0;
        Cursor cursor = db.rawQuery("SELECT cantidad FROM Pagos WHERE id = ?", new String[]{String.valueOf(pagoGastoId)});

        if (cursor.moveToFirst()) {
            cantidad = cursor.getDouble(cursor.getColumnIndexOrThrow("cantidad"));
        }

        cursor.close();
        return cantidad;
    }

}