package application;
public class TestCita {
    public static void main(String[] args) {
        application.controller.CitaController c = new application.controller.CitaController();
        System.out.println(c.obtenerCitaPorId("1"));
    }
}
