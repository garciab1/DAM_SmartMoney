package sv.edu.catolica.dam_smartmoney.Classes;

public class Categoria {
    private String nombre;
    private String imagenURI;

    public Categoria(String nombre, String imagenURI) {
        this.nombre = nombre;
        this.imagenURI = imagenURI;
    }

    public String getNombre() {
        return nombre;
    }

    public String getImagenURI() {
        return imagenURI;
    }
}
