package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BusService {
    private Map<String, String> postaje;
    private Map<String, List<String>> linijeInCasi;
    private Map<String, String> TripsInLinije;

    public BusService() {
        postaje = new HashMap<>();
        linijeInCasi = new HashMap<>();
        TripsInLinije = new HashMap<>();
        beriPostaje("gtfs/stops.txt");
        beriTrips("gtfs/trips.txt");
    }

    private void beriPostaje(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String idPostaje = values[0];
                String imePostaje = values[2];
                postaje.put(idPostaje, imePostaje);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void beriTrips(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String routeId = values[0];
                String tripId = values[2];
                TripsInLinije.put(tripId, routeId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pridobiAvtobuse(String idPostaje) {
        try (BufferedReader br = new BufferedReader(new FileReader("gtfs/stop_times.txt"))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (idPostaje.equals(values[3])) {
                    String trip = values[0];
                    String cas = values[1];
                    String routeId = TripsInLinije.get(trip);
                    if (routeId != null) {
                        linijeInCasi.computeIfAbsent(routeId, k -> new ArrayList<>()).add(cas);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void najdiNaslednjeAvtobuse(String idPostaje, int steviloAvtobusov, boolean b) {
        //izpis postaje
        String imePostaje = postaje.get(idPostaje);
        System.out.println("Postaja: " + imePostaje);
        pridobiAvtobuse(idPostaje);

        LocalTime trenutniCas = LocalTime.now();
        LocalTime dveUriKasneje = trenutniCas.plusHours(2);
        DateTimeFormatter formatCas = DateTimeFormatter.ofPattern("HH:mm");

        for (Map.Entry<String, List<String>> linijaEntry : linijeInCasi.entrySet()) {
            String linija = linijaEntry.getKey();
            List<String> vsiCasi = linijaEntry.getValue();
            Collections.sort(vsiCasi);

            List<String> ustrezniCasi = new ArrayList<>();
            for (String cas : vsiCasi) {
                LocalTime posamezenCas = LocalTime.parse(cas);
                if (!posamezenCas.isBefore(trenutniCas) && !posamezenCas.isAfter(dveUriKasneje)) {
                    if (b) {
                        ustrezniCasi.add(posamezenCas.format(formatCas));
                    } else {
                        long razlikaMinut = Duration.between(trenutniCas, posamezenCas).toMinutes();
                        ustrezniCasi.add(razlikaMinut + "min");
                    }
                    if (ustrezniCasi.size() >= steviloAvtobusov) {
                        break;
                    }
                }
            }
            if (!ustrezniCasi.isEmpty()) {
                System.out.println(linija + ": naslednji avtobusi: " + String.join(", ", ustrezniCasi));
            }
        }
    }
}
