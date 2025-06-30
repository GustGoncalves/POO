package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    private static final String URL = "jdbc:sqlite:eventos.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Tabela de Administradores
            stmt.execute("CREATE TABLE IF NOT EXISTS administradores (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario TEXT NOT NULL UNIQUE," +
                    "senha TEXT NOT NULL)");

            // Tabela de Participantes
            stmt.execute("CREATE TABLE IF NOT EXISTS participantes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT NOT NULL," +
                    "email TEXT NOT NULL UNIQUE," +
                    "usuario TEXT NOT NULL UNIQUE," +
                    "senha TEXT NOT NULL," +
                    "tipo TEXT NOT NULL)");

            // Tabela de Eventos
            stmt.execute("CREATE TABLE IF NOT EXISTS eventos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT NOT NULL," +
                    "descricao TEXT," +
                    "dataInicio TEXT," +
                    "dataFim TEXT)");

            // Tabela de Atividades
            stmt.execute("CREATE TABLE IF NOT EXISTS atividades (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "eventoId INTEGER," +
                    "nome TEXT NOT NULL," +
                    "descricao TEXT," +
                    "data TEXT," +
                    "limiteInscritos INTEGER," +
                    "tipo TEXT," +
                    "FOREIGN KEY(eventoId) REFERENCES eventos(id))");

            // Tabela de Inscrições
            stmt.execute("CREATE TABLE IF NOT EXISTS inscricoes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "participanteId INTEGER," +
                    "eventoId INTEGER," +
                    "atividadeId INTEGER," +
                    "dataInscricao TEXT," +
                    "FOREIGN KEY(participanteId) REFERENCES participantes(id)," +
                    "FOREIGN KEY(eventoId) REFERENCES eventos(id)," +
                    "FOREIGN KEY(atividadeId) REFERENCES atividades(id))");

            // Tabela de Pagamentos
            stmt.execute("CREATE TABLE IF NOT EXISTS pagamentos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "inscricaoId INTEGER," +
                    "valor REAL," +
                    "dataPagamento TEXT," +
                    "status TEXT," +
                    "FOREIGN KEY(inscricaoId) REFERENCES inscricoes(id))");

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
        }
    }
}