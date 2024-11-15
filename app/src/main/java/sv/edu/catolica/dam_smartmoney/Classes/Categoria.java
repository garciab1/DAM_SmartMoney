package sv.edu.catolica.dam_smartmoney.Classes;

public class Categoria {
    private String id; // Cambia el tipo de int a String
    private String nombre;
    private String imagenURI;

    public Categoria(String id, String nombre, String imagenURI) {
        this.id = id;
        this.nombre = nombre;
        this.imagenURI = imagenURI;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagenURI() {
        return imagenURI;
    }
}