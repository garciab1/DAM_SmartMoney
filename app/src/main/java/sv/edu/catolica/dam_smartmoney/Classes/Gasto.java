package sv.edu.catolica.dam_smartmoney.Classes;

public class Gasto {
    private int id;
    private String nombreGasto;
    private double cantidad;
    private int fechaGasto;
    private int categoriaGasto;
    private int pagoGasto;

    public Gasto(int id, String nombreGasto, double cantidad, int fechaGasto, int categoriaGasto, int pagoGasto) {
        this.id = id;
        this.nombreGasto = nombreGasto;
        this.cantidad = cantidad;
        this.fechaGasto = fechaGasto;
        this.categoriaGasto = categoriaGasto;
        this.pagoGasto = pagoGasto;
    }

    public int getId() { return id; }
    public String getNombreGasto() { return nombreGasto; }
    public double getCantidad() { return cantidad; }
    public int getFechaGasto() { return fechaGasto; }
    public int getCategoriaGasto() { return categoriaGasto; }
    public int getPagoGasto() { return pagoGasto; }

    public void setId(int id) { this.id = id; }
    public void setNombreGasto(String nombreGasto) { this.nombreGasto = nombreGasto; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public void setFechaGasto(int fechaGasto) { this.fechaGasto = fechaGasto; }
    public void setCategoriaGasto(int categoriaGasto) { this.categoriaGasto = categoriaGasto; }
    public void setPagoGasto(int pagoGasto) { this.pagoGasto = pagoGasto; }
}
