import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Doacao {
    private String tipo;
    private double quantidade;
    private LocalDate data;

    private static final DateTimeFormatter FORMATADOR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Doacao(String tipo, double quantidade, LocalDate data) {
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.data = data;
    }

    public String getTipo() {
        return tipo;
    }

    public double getQuantidade() {
        return quantidade;
    }

    public LocalDate getData() {
        return data;
    }

  
    public String toString() {
        return "Doação " +
                "Tipo= " + tipo +
                ", Quantidade= " + quantidade +
                ", Data= " + data.format(FORMATADOR);
    }

    public String toCSV() {
        return tipo + "," + quantidade + "," + data.format(FORMATADOR);
    }

    public static Doacao fromCSV(String csv) {
        String[] parts = csv.split(",");
        String tipo = parts[0];
        double quantidade = Double.parseDouble(parts[1]);
        LocalDate data = LocalDate.parse(parts[2], FORMATADOR);
        return new Doacao(tipo, quantidade, data);
    }
}

class SistemaDoacoes {
    private ArrayList<Doacao> listaDoacoes;
    private static final String FILE_PATH = System.getProperty("user.dir") + File.separator + "Doacoes.txt";

    public SistemaDoacoes() {
        listaDoacoes = new ArrayList<>();
    }

    public void adicionarDoacao(Doacao doacao) throws IOException {
        listaDoacoes.add(doacao);
        salvarDoacao(doacao);
    }

    private void salvarDoacao(Doacao doacao) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(doacao.toCSV() + "\n");
        }
    }

    public Map<String, Double> calcularTotalDoacoesPorTipo() {
        Map<String, Double> totaisPorTipo = new HashMap<>();
        for (Doacao doacao : listaDoacoes) {
            totaisPorTipo.put(doacao.getTipo(), totaisPorTipo.getOrDefault(doacao.getTipo(), 0.0) + doacao.getQuantidade());
        }
        return totaisPorTipo;
    }

    public void carregarDoacoes() throws IOException {
        listaDoacoes.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                Doacao doacao = Doacao.fromCSV(linha);
                listaDoacoes.add(doacao);
            }
        }
    }

    public void exibirDoacoesPorTipo(String tipo) {
        for (Doacao doacao : listaDoacoes) {
            if (doacao.getTipo().equalsIgnoreCase(tipo)) {
                System.out.println(doacao);
            }
        }
    }

    public static void main(String[] args) {
        SistemaDoacoes sistema = new SistemaDoacoes();
        Scanner scanner = new Scanner(System.in);

        try {
            sistema.carregarDoacoes();
        } catch (IOException e) {
            System.err.println("Erro ao carregar doações: " + e.getMessage());
        }

        while (true) {
            System.out.println("1. Receber Doação");
            System.out.println("2. Calcular Total de Doações");
            System.out.println("3. Exibir Doações por Tipo");
            System.out.println("4. Sair");
            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();  // Consumir nova linha

            try {
                switch (opcao) {
                    case 1:
                        System.out.print("Tipo da Doação (jeans, moletom, etc.): ");
                        String tipo = scanner.nextLine();
                        System.out.print("Quantidade: ");
                        double quantidade = scanner.nextDouble();
                        scanner.nextLine();  // Consumir nova linha
                        System.out.print("Data (dd/MM/yyyy): ");
                        String dataStr = scanner.nextLine();
                        LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        sistema.adicionarDoacao(new Doacao(tipo, quantidade, data));
                        break;

                    case 2:
                        Map<String, Double> totaisPorTipo = sistema.calcularTotalDoacoesPorTipo();
                        for (Map.Entry<String, Double> entry : totaisPorTipo.entrySet()) {
                            System.out.println("Total de Doações (" + entry.getKey() + "): " + entry.getValue());
                        }
                        break;

                    case 3:
                        System.out.print("Digite o tipo de doação para exibir (jeans, moletom, etc.): ");
                        String tipoExibir = scanner.nextLine();
                        sistema.exibirDoacoesPorTipo(tipoExibir);
                        break;

                    case 4:
                        System.out.println("Saindo...");
                        return;

                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (IOException e) {
                System.err.println("Erro ao manipular arquivo: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
            }
        }
    }
}
