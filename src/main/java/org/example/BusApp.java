package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class BusApp {

    public static void main(String[] args) {
        BusService busService = new BusService();
        busService.najdiNaslednjeAvtobuse("3", 5, false);

    }
}