package dao;

import connection.*;
import model.*;
import exceptions.*;
import java.sql.*;

public class ParticipanteDAO {
    public Participante login(String usuario, String senha) throws SQLException {
        // Normaliza entrada
        usuario = usuario.trim().toLowerCase();
        senha = senha.trim();

        String sql = "SELECT * FROM participantes WHERE LOWER(usuario) = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Se estiver usando hash:
                // if (BCrypt.checkpw(senha, rs.getString("senha"))) {
                // Se estiver em texto puro:
                if (rs.getString("senha").equals(senha)) {
                    Participante participante;
                    String tipo = rs.getString("tipo");

                    switch (tipo.toUpperCase()) {
                        case "ALUNO":
                            participante = new Aluno(
                                    rs.getString("nome"),
                                    rs.getString("email"),
                                    rs.getString("usuario"),
                                    "" // Não retornar a senha no objeto
                            );
                            break;
                        case "PROFESSOR":
                            participante = new Professor(
                                    rs.getString("nome"),
                                    rs.getString("email"),
                                    rs.getString("usuario"),
                                    ""
                            );
                            break;
                        case "PROFISSIONAL":
                            participante = new Profissional(
                                    rs.getString("nome"),
                                    rs.getString("email"),
                                    rs.getString("usuario"),
                                    ""
                            );
                            break;
                        default:
                            return null;
                    }
                    participante.setId(rs.getInt("id"));
                    return participante;
                }
            }
        }
        return null;
    }

    private boolean emailJaExistente(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM participantes WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void validarUsuario(Participante participante)
            throws EmailInvalidoException, EmailDuplicadoException, SenhaFracaException, SQLException {

        // Validação de email
        if (!participante.getEmail().matches("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$")) {
            throw new EmailInvalidoException("Formato de email inválido! Ex: exemplo@email.com");
        }

        if (emailJaExistente(participante.getEmail())) {
            throw new EmailDuplicadoException("O email " + participante.getEmail() + " já está cadastrado!");
        }

        // Validação de senha
        if (!participante.getSenha().matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$")) {
            throw new SenhaFracaException("""
                A senha deve conter:
                - Mínimo 6 caracteres
                - Pelo menos 1 letra maiúscula
                - Pelo menos 1 letra minúscula
                - Pelo menos 1 número""");
        }
    }

    public void cadastrar(Participante participante)
            throws SQLException, EmailInvalidoException, EmailDuplicadoException, SenhaFracaException {

        // Primeiro valida
        validarUsuario(participante);

        // Se passou nas validações, prossegue com o cadastro
        String sql = "INSERT INTO participantes (nome, email, usuario, senha, tipo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, participante.getNome());
            stmt.setString(2, participante.getEmail());
            stmt.setString(3, participante.getUsuario());
            stmt.setString(4, participante.getSenha());
            stmt.setString(5, participante.getTipo());
            stmt.executeUpdate();
        }
    }

    public void registrarPagamento(int participanteId, int inscricaoId)
            throws SQLException, PagamentoInvalidoException, EntidadeNaoEncontradaException {

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            // Verificar se a inscrição existe e pertence ao participante
            String sqlVerifica = "SELECT id FROM inscricoes WHERE id = ? AND participanteId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlVerifica)) {
                stmt.setInt(1, inscricaoId);
                stmt.setInt(2, participanteId);
                if (!stmt.executeQuery().next()) {
                    throw new EntidadeNaoEncontradaException("Inscrição não encontrada ou não pertence ao participante");
                }
            }

            // Obter o tipo do participante e valor
            String tipo = getTipoParticipante(participanteId);
            double valor = new ValorInscricaoDAO().getValorPorTipo(tipo);

            if (valor <= 0) {
                throw new PagamentoInvalidoException("Valor de pagamento inválido");
            }

            // Registrar pagamento
            String sqlPagamento = "INSERT INTO pagamentos (inscricaoId, valor, dataPagamento, status) VALUES (?, ?, datetime('now'), 'PENDENTE')";
            try (PreparedStatement stmt = conn.prepareStatement(sqlPagamento)) {
                stmt.setInt(1, inscricaoId);
                stmt.setDouble(2, valor);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException | EntidadeNaoEncontradaException | PagamentoInvalidoException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private String getTipoParticipante(int participanteId) throws SQLException {
        String sql = "SELECT tipo FROM participantes WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, participanteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("tipo");
            }
            throw new SQLException("Participante não encontrado");
        }
    }
}