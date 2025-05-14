import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class ConversorDeMoedas {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== CONVERSOR DE MOEDAS ===");
        System.out.println("Escolha uma opção de conversão:");
        System.out.println("1 - USD para BRL (Real)");
        System.out.println("2 - USD para EUR (Euro)");
        System.out.println("3 - USD para JPY (Iene)");
        System.out.println("4 - USD para GBP (Libra)");
        System.out.println("5 - USD para CAD (Dólar Canadense)");
        System.out.println("6 - BRL para USD (Dólar Americano)");

        int opcao = scanner.nextInt();

        String moedaBase = "USD";
        String moedaDestino = "";
        double valor = 0.0;

        if (opcao >= 1 && opcao <= 5) {
            System.out.print("Digite o valor em USD: ");
            valor = scanner.nextDouble();

            if (opcao == 1) moedaDestino = "BRL";
            else if (opcao == 2) moedaDestino = "EUR";
            else if (opcao == 3) moedaDestino = "JPY";
            else if (opcao == 4) moedaDestino = "GBP";
            else if (opcao == 5) moedaDestino = "CAD";
        } else if (opcao == 6) {
            moedaBase = "BRL";
            moedaDestino = "USD";
            System.out.print("Digite o valor em BRL: ");
            valor = scanner.nextDouble();
        } else {
            System.out.println("Opção inválida!");
            scanner.close();
            return;
        }

        try {
            String urlStr = "https://v6.exchangerate-api.com/v6/7e647369d31b04f7d87582a7/latest/" + moedaBase;

            URL url = new URL(urlStr);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");

            int codigoResposta = conexao.getResponseCode();

            if (codigoResposta == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                StringBuilder resposta = new StringBuilder();
                String linha;

                while ((linha = reader.readLine()) != null) {
                    resposta.append(linha);
                }
                reader.close();

                String json = resposta.toString();

                // Busca simples da taxa
                String busca = "\"" + moedaDestino + "\":";
                int index = json.indexOf(busca);

                if (index != -1) {
                    int start = index + busca.length();
                    int end = json.indexOf(",", start);
                    if (end == -1) end = json.indexOf("}", start);

                    String valorTaxaStr = json.substring(start, end).trim();
                    valorTaxaStr = valorTaxaStr.replace(",", ".");

                    double taxa = Double.parseDouble(valorTaxaStr);
                    double resultado = valor * taxa;

                    System.out.printf("Valor convertido: %.2f %s\n", resultado, moedaDestino);
                } else {
                    System.out.println("Moeda não encontrada na resposta.");
                }

            } else {
                System.out.println("Erro ao acessar API. Código: " + codigoResposta);
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }

        scanner.close();
    }
}
