package view;

import connection.*;
import dao.*;
import exceptions.*;
import model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class MainGUI {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    private JFrame frame;
    private JPanel currentPanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // DAOs (mantive os mesmos que você já tinha)
    private static AdministradorDAO administradorDAO = new AdministradorDAO();
    private static ParticipanteDAO participanteDAO = new ParticipanteDAO();
    private static EventoDAO eventoDAO = new EventoDAO();
    private static AtividadeDAO atividadeDAO = new AtividadeDAO();
    private static InscricaoDAO inscricaoDAO = new InscricaoDAO();
    private static PagamentoDAO pagamentoDAO = new PagamentoDAO();
    private static ValorInscricaoDAO valorInscricaoDAO = new ValorInscricaoDAO();

    // Usuários logados
    private Administrador adminLogado = null;
    private Participante participanteLogado = null;

    // Cores do tema
    private final Color PRIMARY_COLOR = new Color(0, 102, 204);
    private final Color SECONDARY_COLOR = new Color(240, 240, 240);
    private final Color ACCENT_COLOR = new Color(255, 153, 0);

    public static void main(String[] args) {
        try {
            Database.initializeDatabase();
            SwingUtilities.invokeLater(() -> new MainGUI().initialize());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao inicializar o banco de dados: " + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void initialize() {
        frame = new JFrame("Sistema de Gerenciamento de Eventos Acadêmicos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 650);
        frame.setMinimumSize(new Dimension(800, 600));

        // Centraliza a janela
        frame.setLocationRelativeTo(null);

        // Configura o layout principal
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Adiciona os painéis
        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createAdminPanel(), "admin");
        mainPanel.add(createParticipantPanel(), "participant");
        mainPanel.add(createRegisterPanel(), "register");

        frame.add(mainPanel);
        showLoginPanel();
        frame.setVisible(true);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel de conteúdo centralizado
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(SECONDARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título
        JLabel titleLabel = new JLabel("Sistema de Eventos Acadêmicos");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        centerPanel.add(titleLabel, gbc);

        // Subtítulo
        JLabel subtitleLabel = new JLabel("Faça login para continuar");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        centerPanel.add(subtitleLabel, gbc);

        // Painel do formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.gridwidth = GridBagConstraints.REMAINDER;
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.insets = new Insets(5, 5, 5, 5);

        // Campos do formulário
        JLabel userLabel = new JLabel("Usuário*");
        JTextField userField = new JTextField(20);
        formPanel.add(userLabel, formGbc);
        formPanel.add(userField, formGbc);

        JLabel passLabel = new JLabel("Senha*");
        JPasswordField passField = new JPasswordField(20);
        formPanel.add(passLabel, formGbc);
        formPanel.add(passField, formGbc);

        // Botão de login
        JButton loginButton = new JButton("Entrar");
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.addActionListener(e -> {
            try {
                if (!validarCamposObrigatorios(panel, userField, passField)) {
                    return;
                }

                Usuario usuario = usuarioDAO.login(userField.getText(), new String(passField.getPassword()));

                if (usuario != null) {
                    if (usuario.isAdmin()) {
                        adminLogado = (Administrador) usuario;
                        showAdminPanel();
                    } else {
                        participanteLogado = (Participante) usuario;
                        showParticipantPanel();
                    }
                } else {
                    JOptionPane.showMessageDialog(frame,
                            "Usuário ou senha incorretos!",
                            "Erro de Login", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Erro ao conectar com o banco de dados: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        formPanel.add(loginButton, formGbc);

        // Botão de cadastro
        JButton registerButton = new JButton("Cadastrar-se como Participante");
        registerButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        registerButton.addActionListener(e -> cardLayout.show(mainPanel, "register"));
        formPanel.add(registerButton, formGbc);

        centerPanel.add(formPanel, gbc);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUserLoginForm(String userType) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel userLabel = new JLabel("Usuário*");
        JTextField userField = new JTextField(20);
        panel.add(userLabel, gbc);
        panel.add(userField, gbc);

        JLabel passLabel = new JLabel("Senha*");
        JPasswordField passField = new JPasswordField(20);
        panel.add(passLabel, gbc);
        panel.add(passField, gbc);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.addActionListener(e -> {
            String usuario = userField.getText();
            String senha = new String(passField.getPassword());

            try {
                if (!validarCamposObrigatorios(panel, userField, passField)) {
                    return;
                }
                if (userType.equals("admin")) {
                    adminLogado = administradorDAO.login(usuario, senha);
                    if (adminLogado != null) {
                        showAdminPanel();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Usuário ou senha incorretos!",
                                "Erro de Login", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    participanteLogado = participanteDAO.login(usuario, senha);
                    if (participanteLogado != null) {
                        showParticipantPanel();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Usuário ou senha incorretos!",
                                "Erro de Login", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao conectar com o banco de dados: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(loginButton, gbc);
        return panel;
    }

    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(SECONDARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Painel central
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(SECONDARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Título
        JLabel titleLabel = new JLabel("Cadastro de Participante");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(PRIMARY_COLOR);
        centerPanel.add(titleLabel, gbc);

        // Campos do formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Nome
        JLabel nomeLabel = new JLabel("Nome Completo*");
        JTextField nomeField = new JTextField(25);
        formPanel.add(nomeLabel, gbc);
        formPanel.add(nomeField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email*");
        JTextField emailField = new JTextField(25);
        emailField.setToolTipText("Formato válido: usuario@dominio.com");
        formPanel.add(emailLabel, gbc);
        formPanel.add(emailField, gbc);

        // Usuário
        JLabel usuarioLabel = new JLabel("Usuário*");
        JTextField usuarioField = new JTextField(25);
        formPanel.add(usuarioLabel, gbc);
        formPanel.add(usuarioField, gbc);

        // Senha
        JLabel senhaLabel = new JLabel("Senha*");
        JPasswordField senhaField = new JPasswordField(25);
        senhaField.setToolTipText("<html>Requisitos:<br>- 6+ caracteres<br>- 1 maiúscula<br>- 1 minúscula<br>- 1 número</html>");
        formPanel.add(senhaLabel, gbc);
        formPanel.add(senhaField, gbc);

        // Confirmação de Senha
        JLabel confirmaSenhaLabel = new JLabel("Confirmar Senha*");
        JPasswordField confirmaSenhaField = new JPasswordField(25);
        formPanel.add(confirmaSenhaLabel, gbc);
        formPanel.add(confirmaSenhaField, gbc);

        // Tipo de Participante
        JLabel tipoLabel = new JLabel("Tipo de Participante*");
        JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"Aluno", "Professor", "Profissional"});
        formPanel.add(tipoLabel, gbc);
        formPanel.add(tipoCombo, gbc);

        // Botão de Cadastro
        JButton registerButton = new JButton("Cadastrar");
        registerButton.setBackground(PRIMARY_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Ação do botão
        registerButton.addActionListener(e -> {
            try {
                // Validação básica de campos obrigatórios
                if (!validarCamposObrigatorios(formPanel, nomeField, emailField, usuarioField, senhaField, confirmaSenhaField)) {
                    return;
                }

                // Verifica se as senhas coincidem
                if (!new String(senhaField.getPassword()).equals(new String(confirmaSenhaField.getPassword()))) {
                    JOptionPane.showMessageDialog(panel, "As senhas não coincidem!",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                    confirmaSenhaField.requestFocus();
                    return;
                }

                // Cria o participante
                Participante participante = new Participante(
                        nomeField.getText().trim(),
                        emailField.getText().trim(),
                        usuarioField.getText().trim(),
                        new String(senhaField.getPassword()),
                        (String) tipoCombo.getSelectedItem()
                );

                // Valida e cadastra
                participanteDAO.validarUsuario(participante);
                participanteDAO.cadastrar(participante);

                JOptionPane.showMessageDialog(panel, "Cadastro realizado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                showLoginPanel();

            } catch (EmailInvalidoException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(),
                        "Email Inválido", JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
            } catch (EmailDuplicadoException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(),
                        "Email Existente", JOptionPane.ERROR_MESSAGE);
                emailField.requestFocus();
            } catch (SenhaFracaException ex) {
                JOptionPane.showMessageDialog(panel, ex.getMessage(),
                        "Senha Fraca", JOptionPane.ERROR_MESSAGE);
                senhaField.requestFocus();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel, "Erro no banco de dados: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Botão Voltar
        JButton backButton = new JButton("Voltar");
        backButton.addActionListener(e -> showLoginPanel());

        // Painel de botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(backButton);
        buttonPanel.add(registerButton);

        formPanel.add(buttonPanel, gbc);
        centerPanel.add(formPanel, gbc);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAdminPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Barra de navegação superior
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Administrador: " + (adminLogado != null ? adminLogado.getUsuario() : ""));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(welcomeLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            adminLogado = null;
            showLoginPanel();
        });
        topPanel.add(logoutButton);

        panel.add(topPanel, BorderLayout.NORTH);

        // Menu lateral
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(240, 240, 240));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {
                "Criar Evento", "Listar Eventos", "Editar Evento", "Excluir Evento",
                "Adicionar Atividade", "Participantes por Evento", "Participantes por Atividade",
                "Todas Inscrições", "Confirmar Pagamento", "Valores de Inscrição"
        };

        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            button.addActionListener(new AdminMenuListener(item));
            menuPanel.add(button);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        panel.add(menuPanel, BorderLayout.WEST);

        // Painel de conteúdo
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Adiciona um painel vazio inicial
        contentPanel.add(new JLabel("Selecione uma opção no menu à esquerda", JLabel.CENTER), BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createParticipantPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Barra de navegação superior
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel welcomeLabel = new JLabel("Participante: " + (participanteLogado != null ? participanteLogado.getNome() : ""));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(welcomeLabel);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            participanteLogado = null;
            showLoginPanel();
        });
        topPanel.add(logoutButton);

        panel.add(topPanel, BorderLayout.NORTH);

        // Menu lateral
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(240, 240, 240));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setPreferredSize(new Dimension(200, 0));

        String[] menuItems = {
                "Listar Eventos", "Inscrever-se em Evento", "Inscrever-se em Atividade",
                "Minhas Inscrições", "Registrar Pagamento"
        };

        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            button.addActionListener(new ParticipantMenuListener(item));
            menuPanel.add(button);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        panel.add(menuPanel, BorderLayout.WEST);

        // Painel de conteúdo
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Adiciona um painel vazio inicial
        contentPanel.add(new JLabel("Selecione uma opção no menu à esquerda", JLabel.CENTER), BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showLoginPanel() {
        cardLayout.show(mainPanel, "login");
        frame.setTitle("Sistema de Gerenciamento de Eventos Acadêmicos - Login");
    }

    private void showAdminPanel() {
        cardLayout.show(mainPanel, "admin");
        frame.setTitle("Sistema de Gerenciamento de Eventos Acadêmicos - Administrador");
    }

    private void showParticipantPanel() {
        cardLayout.show(mainPanel, "participant");
        frame.setTitle("Sistema de Gerenciamento de Eventos Acadêmicos - Participante");
    }

    // Classe interna para lidar com ações do menu do administrador
    private class AdminMenuListener implements ActionListener {
        private String menuItem;

        public AdminMenuListener(String menuItem) {
            this.menuItem = menuItem;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel adminPanel = (JPanel) mainPanel.getComponent(1); // Painel do admin é o segundo
            JPanel contentPanel = (JPanel) adminPanel.getComponent(2); // Painel de conteúdo é o terceiro

            contentPanel.removeAll();

            try {
                switch (menuItem) {
                    case "Criar Evento":
                        contentPanel.add(createEventCreationPanel());
                        break;
                    case "Listar Eventos":
                        contentPanel.add(createEventListPanel());
                        break;
                    case "Editar Evento":
                        contentPanel.add(createEventEditPanel());
                        break;
                    case "Excluir Evento":
                        contentPanel.add(createEventDeletePanel());
                        break;
                    case "Adicionar Atividade":
                        contentPanel.add(createActivityAddPanel());
                        break;
                    case "Participantes por Evento":
                        contentPanel.add(createParticipantsByEventPanel());
                        break;
                    case "Participantes por Atividade":
                        contentPanel.add(createParticipantsByActivityPanel());
                        break;
                    case "Todas Inscrições":
                        contentPanel.add(createAllRegistrationsPanel());
                        break;
                    case "Confirmar Pagamento":
                        contentPanel.add(createPaymentConfirmationPanel());
                        break;
                    case "Valores de Inscrição":
                        contentPanel.add(createSubscriptionValuesPanel());
                        break;
                }
            } catch (SQLException | EntidadeNaoEncontradaException ex) {
                JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }

            contentPanel.revalidate();
            contentPanel.repaint();
        }

        private JPanel createEventEditPanel() throws SQLException, EntidadeNaoEncontradaException {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Lista de eventos para seleção
            List<Evento> eventos = eventoDAO.listarTodos();
            DefaultComboBoxModel<Evento> comboModel = new DefaultComboBoxModel<>();
            for (Evento evento : eventos) {
                comboModel.addElement(evento);
            }

            JComboBox<Evento> eventCombo = new JComboBox<>(comboModel);
            JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectionPanel.add(new JLabel("Selecione o evento para editar:"));
            selectionPanel.add(eventCombo);
            panel.add(selectionPanel, BorderLayout.NORTH);

            // Formulário de edição
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            JTextField nomeField = new JTextField(30);
            JTextArea descricaoArea = new JTextArea(5, 30);
            descricaoArea.setLineWrap(true);
            JScrollPane descricaoScroll = new JScrollPane(descricaoArea);
            JTextField dataInicioField = new JTextField(10);
            JTextField dataFimField = new JTextField(10);

            // Botão para carregar dados
            JButton loadButton = new JButton("Carregar Dados");
            loadButton.addActionListener(e -> {
                Evento selected = (Evento) eventCombo.getSelectedItem();
                nomeField.setText(selected.getNome());
                descricaoArea.setText(selected.getDescricao());
                dataInicioField.setText(selected.getDataInicio());
                dataFimField.setText(selected.getDataFim());
            });
            selectionPanel.add(loadButton);

            formPanel.add(new JLabel("Nome do Evento*"), gbc);
            formPanel.add(nomeField, gbc);
            formPanel.add(new JLabel("Descrição"), gbc);
            formPanel.add(descricaoScroll, gbc);
            formPanel.add(new JLabel("Data de Início (YYYY-MM-DD)*"), gbc);
            formPanel.add(dataInicioField, gbc);
            formPanel.add(new JLabel("Data de Fim (YYYY-MM-DD)*"), gbc);
            formPanel.add(dataFimField, gbc);

            JButton saveButton = new JButton("Salvar Alterações");
            saveButton.setBackground(PRIMARY_COLOR);
            saveButton.setForeground(Color.WHITE);
            saveButton.addActionListener(e -> {
                Evento selected = (Evento) eventCombo.getSelectedItem();
                try {
                    if (!validarCamposObrigatorios(panel, nomeField, dataInicioField, dataFimField)) {
                        return;
                    }

                    Evento updated = new Evento(
                            nomeField.getText(),
                            descricaoArea.getText(),
                            dataInicioField.getText(),
                            dataFimField.getText()
                    );
                    updated.setId(selected.getId());
                    eventoDAO.atualizar(updated);
                    JOptionPane.showMessageDialog(panel, "Evento atualizado com sucesso!");
                    eventCombo.setSelectedItem(updated); // Atualiza no combo
                } catch (SQLException | EntidadeNaoEncontradaException ex) {
                    JOptionPane.showMessageDialog(panel, "Erro ao atualizar: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            formPanel.add(saveButton, gbc);
            panel.add(formPanel, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createEventDeletePanel() throws SQLException {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Lista de eventos para seleção
            List<Evento> eventos = eventoDAO.listarTodos();
            DefaultComboBoxModel<Evento> comboModel = new DefaultComboBoxModel<>();
            for (Evento evento : eventos) {
                comboModel.addElement(evento);
            }

            JComboBox<Evento> eventCombo = new JComboBox<>(comboModel);
            JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectionPanel.add(new JLabel("Selecione o evento para excluir:"));
            selectionPanel.add(eventCombo);
            panel.add(selectionPanel, BorderLayout.NORTH);

            // Painel de confirmação
            JPanel confirmPanel = new JPanel();
            JButton deleteButton = new JButton("Excluir Evento");
            deleteButton.setBackground(new Color(204, 0, 0)); // Vermelho
            deleteButton.setForeground(Color.WHITE);
            deleteButton.addActionListener(e -> {
                Evento selected = (Evento) eventCombo.getSelectedItem();
                int confirm = JOptionPane.showConfirmDialog(panel,
                        "Tem certeza que deseja excluir o evento '" + selected.getNome() + "'?\nEsta ação não pode ser desfeita.",
                        "Confirmar Exclusão", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        eventoDAO.excluir(selected.getId());
                        comboModel.removeElement(selected);
                        JOptionPane.showMessageDialog(panel, "Evento excluído com sucesso!");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(panel, "Erro ao excluir: " + ex.getMessage(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            confirmPanel.add(deleteButton);
            panel.add(confirmPanel, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createActivityAddPanel() throws SQLException {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Lista de eventos para seleção
            List<Evento> eventos = eventoDAO.listarTodos();
            DefaultComboBoxModel<Evento> comboModel = new DefaultComboBoxModel<>();
            for (Evento evento : eventos) {
                comboModel.addElement(evento);
            }

            JComboBox<Evento> eventCombo = new JComboBox<>(comboModel);
            JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectionPanel.add(new JLabel("Selecione o evento:"));
            selectionPanel.add(eventCombo);
            panel.add(selectionPanel, BorderLayout.NORTH);

            // Formulário de atividade
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            JTextField nomeField = new JTextField(30);
            JTextArea descricaoArea = new JTextArea(5, 30);
            descricaoArea.setLineWrap(true);
            JScrollPane descricaoScroll = new JScrollPane(descricaoArea);
            JTextField dataField = new JTextField(10);
            JSpinner limiteSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
            JComboBox<String> tipoCombo = new JComboBox<>(new String[]{
                    "Palestra", "Workshop", "Mesa Redonda", "Minicurso", "Apresentação"
            });

            formPanel.add(new JLabel("Nome da Atividade*"), gbc);
            formPanel.add(nomeField, gbc);
            formPanel.add(new JLabel("Descrição"), gbc);
            formPanel.add(descricaoScroll, gbc);
            formPanel.add(new JLabel("Data (YYYY-MM-DD)*"), gbc);
            formPanel.add(dataField, gbc);
            formPanel.add(new JLabel("Limite de Participantes*"), gbc);
            formPanel.add(limiteSpinner, gbc);
            formPanel.add(new JLabel("Tipo de Atividade*"), gbc);
            formPanel.add(tipoCombo, gbc);

            JButton addButton = new JButton("Adicionar Atividade");
            addButton.setBackground(PRIMARY_COLOR);
            addButton.setForeground(Color.WHITE);
            addButton.addActionListener(e -> {
                Evento selectedEvent = (Evento) eventCombo.getSelectedItem();
                try {
                    if (!validarCamposObrigatorios(panel, nomeField, dataField, tipoCombo)) {
                        return;
                    }

                    LocalDate dataAtividade = LocalDate.parse(dataField.getText());
                    LocalDate inicioEvento = LocalDate.parse(selectedEvent.getDataInicio());
                    LocalDate fimEvento = LocalDate.parse(selectedEvent.getDataFim());

                    if (dataAtividade.isBefore(inicioEvento) || dataAtividade.isAfter(fimEvento)) {
                        JOptionPane.showMessageDialog(panel,
                                "Data da atividade deve estar entre " + selectedEvent.getDataInicio() +
                                        " e " + selectedEvent.getDataFim(),
                                "Erro de Data", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Atividade atividade = new Atividade(
                            selectedEvent.getId(),
                            nomeField.getText(),
                            descricaoArea.getText(),
                            dataField.getText(),
                            (Integer) limiteSpinner.getValue(),
                            (String) tipoCombo.getSelectedItem()
                    );
                    atividadeDAO.salvar(atividade);
                    JOptionPane.showMessageDialog(panel, "Atividade adicionada com sucesso!");
                    // Limpa os campos
                    nomeField.setText("");
                    descricaoArea.setText("");
                    dataField.setText("");
                    limiteSpinner.setValue(10);
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Formato de data inválido. Use AAAA-MM-DD",
                            "Erro", JOptionPane.ERROR_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel, "Erro ao adicionar atividade: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            formPanel.add(addButton, gbc);
            panel.add(formPanel, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createParticipantsByEventPanel() throws SQLException {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Lista de eventos para seleção
            List<Evento> eventos = eventoDAO.listarTodos();
            DefaultComboBoxModel<Evento> comboModel = new DefaultComboBoxModel<>();
            for (Evento evento : eventos) {
                comboModel.addElement(evento);
            }

            JComboBox<Evento> eventCombo = new JComboBox<>(comboModel);
            JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectionPanel.add(new JLabel("Selecione o evento:"));
            selectionPanel.add(eventCombo);
            panel.add(selectionPanel, BorderLayout.NORTH);

            // Tabela de participantes
            JTable participantsTable = new JTable();
            JScrollPane scrollPane = new JScrollPane(participantsTable);
            panel.add(scrollPane, BorderLayout.CENTER);

            // Botão para carregar participantes
            JButton loadButton = new JButton("Carregar Participantes");
            loadButton.addActionListener(e -> {
                Evento selected = (Evento) eventCombo.getSelectedItem();
                try {
                    List<Participante> participantes = administradorDAO.listarParticipantesPorEvento(selected.getId());

                    String[] columnNames = {"ID", "Nome", "Email", "Tipo"};
                    Object[][] data = new Object[participantes.size()][4];

                    for (int i = 0; i < participantes.size(); i++) {
                        Participante p = participantes.get(i);
                        data[i][0] = p.getId();
                        data[i][1] = p.getNome();
                        data[i][2] = p.getEmail();
                        data[i][3] = p.getTipo();
                    }

                    participantsTable.setModel(new DefaultTableModel(data, columnNames));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel, "Erro ao carregar participantes: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            selectionPanel.add(loadButton);

            return panel;
        }

        private JPanel createParticipantsByActivityPanel() throws SQLException {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Lista de eventos para seleção
            List<Evento> eventos = eventoDAO.listarTodos();
            DefaultComboBoxModel<Evento> eventComboModel = new DefaultComboBoxModel<>();
            for (Evento evento : eventos) {
                eventComboModel.addElement(evento);
            }

            JComboBox<Evento> eventCombo = new JComboBox<>(eventComboModel);
            JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            selectionPanel.add(new JLabel("Selecione o evento:"));
            selectionPanel.add(eventCombo);

            // Lista de atividades (será atualizada quando selecionar um evento)
            DefaultComboBoxModel<Atividade> activityComboModel = new DefaultComboBoxModel<>();
            JComboBox<Atividade> activityCombo = new JComboBox<>(activityComboModel);
            selectionPanel.add(new JLabel("Selecione a atividade:"));
            selectionPanel.add(activityCombo);

            // Atualiza atividades quando seleciona um evento
            eventCombo.addActionListener(e -> {
                Evento selected = (Evento) eventCombo.getSelectedItem();
                if (selected != null) {
                    try {
                        List<Atividade> atividades = atividadeDAO.listarPorEvento(selected.getId());
                        activityComboModel.removeAllElements();
                        for (Atividade atividade : atividades) {
                            activityComboModel.addElement(atividade);
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(panel, "Erro ao carregar atividades: " + ex.getMessage(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            panel.add(selectionPanel, BorderLayout.NORTH);

            // Tabela de participantes
            JTable participantsTable = new JTable();
            JScrollPane scrollPane = new JScrollPane(participantsTable);
            panel.add(scrollPane, BorderLayout.CENTER);

            // Botão para carregar participantes
            JButton loadButton = new JButton("Carregar Participantes");
            loadButton.addActionListener(e -> {
                Atividade selected = (Atividade) activityCombo.getSelectedItem();
                if (selected == null) return;

                try {
                    List<Participante> participantes = administradorDAO.listarParticipantesPorAtividade(selected.getId());

                    String[] columnNames = {"ID", "Nome", "Email", "Tipo"};
                    Object[][] data = new Object[participantes.size()][4];

                    for (int i = 0; i < participantes.size(); i++) {
                        Participante p = participantes.get(i);
                        data[i][0] = p.getId();
                        data[i][1] = p.getNome();
                        data[i][2] = p.getEmail();
                        data[i][3] = p.getTipo();
                    }

                    participantsTable.setModel(new DefaultTableModel(data, columnNames));
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel, "Erro ao carregar participantes: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            selectionPanel.add(loadButton);

            return panel;
        }

        private JPanel createAllRegistrationsPanel() throws SQLException {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel("Todas as Inscrições");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            panel.add(titleLabel, BorderLayout.NORTH);

            List<InscricaoDAO.InscricaoInfo> inscricoes = inscricaoDAO.listarTodas();
            String[] columnNames = {"ID", "Participante", "Evento", "Atividade", "Data", "Status", "Valor"};
            Object[][] data = new Object[inscricoes.size()][7];

            for (int i = 0; i < inscricoes.size(); i++) {
                InscricaoDAO.InscricaoInfo inscricao = inscricoes.get(i);
                data[i][0] = inscricao.getId();
                data[i][1] = inscricao.getParticipante();
                data[i][2] = inscricao.getEvento();
                data[i][3] = inscricao.getAtividade();
                data[i][4] = inscricao.getDataInscricao();
                data[i][5] = inscricao.getStatusPagamento() == null ? "PENDENTE" : inscricao.getStatusPagamento();
                data[i][6] = inscricao.getValor();
            }

            JTable table = new JTable(data, columnNames);
            table.setFillsViewportHeight(true);
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);

            return panel;
        }

        private JPanel createPaymentConfirmationPanel() throws SQLException {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel titleLabel = new JLabel("Confirmar Pagamentos Pendentes");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            panel.add(titleLabel, BorderLayout.NORTH);

            List<InscricaoDAO.InscricaoInfo> inscricoes = inscricaoDAO.listarComPagamentoPendente();
            String[] columnNames = {"ID", "Participante", "Evento", "Atividade", "Valor"};
            Object[][] data = new Object[inscricoes.size()][5];

            for (int i = 0; i < inscricoes.size(); i++) {
                InscricaoDAO.InscricaoInfo inscricao = inscricoes.get(i);
                data[i][0] = inscricao.getId();
                data[i][1] = inscricao.getParticipante();
                data[i][2] = inscricao.getEvento();
                data[i][3] = inscricao.getAtividade();
                data[i][4] = inscricao.getValor();
            }

            JTable table = new JTable(data, columnNames);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollPane = new JScrollPane(table);
            panel.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            JButton confirmButton = new JButton("Confirmar Pagamento Selecionado");
            confirmButton.setBackground(PRIMARY_COLOR);
            confirmButton.setForeground(Color.WHITE);
            confirmButton.addActionListener(e -> {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel, "Selecione uma inscrição para confirmar o pagamento",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int inscricaoId = (int) table.getValueAt(selectedRow, 0);
                try {
                    pagamentoDAO.confirmarPagamento(inscricaoId);
                    JOptionPane.showMessageDialog(panel, "Pagamento confirmado com sucesso!");

                    // Atualiza a tabela
                    List<InscricaoDAO.InscricaoInfo> updatedInscricoes = inscricaoDAO.listarComPagamentoPendente();
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    model.setRowCount(0); // Limpa a tabela

                    for (InscricaoDAO.InscricaoInfo inscricao : updatedInscricoes) {
                        model.addRow(new Object[]{
                                inscricao.getId(),
                                inscricao.getParticipante(),
                                inscricao.getEvento(),
                                inscricao.getAtividade(),
                                inscricao.getValor()
                        });
                    }
                } catch (SQLException | EntidadeNaoEncontradaException ex) {
                    JOptionPane.showMessageDialog(panel, "Erro ao confirmar pagamento: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            buttonPanel.add(confirmButton);
            panel.add(buttonPanel, BorderLayout.SOUTH);

            return panel;
        }

        private JPanel createSubscriptionValuesPanel() throws SQLException {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 5, 5, 5);

            JLabel titleLabel = new JLabel("Definir Valores de Inscrição");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            panel.add(titleLabel, gbc);

            ValorInscricaoDAO valorDAO = new ValorInscricaoDAO();

            // Inicializa valores se a tabela estiver vazia
            try {
                valorDAO.inicializarValoresPadrao();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(panel, "Erro ao inicializar valores padrão: " + e.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }

            // Obtém os valores atuais
            double valorAluno = valorDAO.getValorPorTipo("ALUNO");
            double valorProfessor = valorDAO.getValorPorTipo("PROFESSOR");
            double valorProfissional = valorDAO.getValorPorTipo("PROFISSIONAL");

            // Campos para edição
            JSpinner alunoSpinner = new JSpinner(new SpinnerNumberModel(valorAluno, 0, 1000, 10));
            JSpinner professorSpinner = new JSpinner(new SpinnerNumberModel(valorProfessor, 0, 1000, 10));
            JSpinner profissionalSpinner = new JSpinner(new SpinnerNumberModel(valorProfissional, 0, 1000, 10));

            panel.add(new JLabel("Valor para Alunos:"), gbc);
            panel.add(alunoSpinner, gbc);
            panel.add(new JLabel("Valor para Professores:"), gbc);
            panel.add(professorSpinner, gbc);
            panel.add(new JLabel("Valor para Profissionais:"), gbc);
            panel.add(profissionalSpinner, gbc);

            JButton saveButton = new JButton("Salvar Valores");
            saveButton.setBackground(PRIMARY_COLOR);
            saveButton.setForeground(Color.WHITE);
            saveButton.addActionListener(e -> {
                try {
                    valorDAO.atualizarValor("ALUNO", (Double) alunoSpinner.getValue());
                    valorDAO.atualizarValor("PROFESSOR", (Double) professorSpinner.getValue());
                    valorDAO.atualizarValor("PROFISSIONAL", (Double) profissionalSpinner.getValue());

                    JOptionPane.showMessageDialog(panel, "Valores atualizados com sucesso!",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel, "Erro ao atualizar valores: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            panel.add(saveButton, gbc);

            return panel;
        }
    }

    // Classe interna para lidar com ações do menu do participante
    private class ParticipantMenuListener implements ActionListener {
        private String menuItem;

        public ParticipantMenuListener(String menuItem) {
            this.menuItem = menuItem;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel participantPanel = (JPanel) mainPanel.getComponent(2); // Painel do participante é o terceiro
            JPanel contentPanel = (JPanel) participantPanel.getComponent(2); // Painel de conteúdo é o terceiro

            contentPanel.removeAll();

            try {
                switch (menuItem) {
                    case "Listar Eventos":
                        contentPanel.add(createEventListPanel());
                        break;
                    case "Inscrever-se em Evento":
                        contentPanel.add(createEventRegistrationPanel());
                        break;
                    case "Inscrever-se em Atividade":
                        contentPanel.add(createActivityRegistrationPanel());
                        break;
                    case "Minhas Inscrições":
                        contentPanel.add(createMyRegistrationsPanel());
                        break;
                    case "Registrar Pagamento":
                        contentPanel.add(createPaymentRegistrationPanel());
                        break;
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }

            contentPanel.revalidate();
            contentPanel.repaint();
        }
    }

    // Métodos para criar os painéis de conteúdo específicos
    private JPanel createEventCreationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Criar Novo Evento");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(titleLabel, gbc);

        JTextField nomeField = new JTextField(30);
        JTextArea descricaoArea = new JTextArea(5, 30);
        descricaoArea.setLineWrap(true);
        JScrollPane descricaoScroll = new JScrollPane(descricaoArea);
        JTextField dataInicioField = new JTextField(10);
        JTextField dataFimField = new JTextField(10);

        panel.add(new JLabel("Nome do Evento*"), gbc);
        panel.add(nomeField, gbc);
        panel.add(new JLabel("Descrição"), gbc);
        panel.add(descricaoScroll, gbc);
        panel.add(new JLabel("Data de Início (YYYY-MM-DD)*"), gbc);
        panel.add(dataInicioField, gbc);
        panel.add(new JLabel("Data de Fim (YYYY-MM-DD)*"), gbc);
        panel.add(dataFimField, gbc);

        JButton createButton = new JButton("Criar Evento");
        createButton.setBackground(PRIMARY_COLOR);
        createButton.setForeground(Color.WHITE);
        createButton.addActionListener(e -> {
            try {
                if (!validarCamposObrigatorios(panel, nomeField, dataInicioField, dataFimField)) {
                    return;
                }

                if (!validarDatas(dataInicioField.getText(), dataFimField.getText(), panel)) {
                    return;
                }

                Evento evento = new Evento(
                        nomeField.getText(),
                        descricaoArea.getText(),
                        dataInicioField.getText(),
                        dataFimField.getText()
                );
                eventoDAO.salvar(evento);
                JOptionPane.showMessageDialog(frame, "Evento criado com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                nomeField.setText("");
                descricaoArea.setText("");
                dataInicioField.setText("");
                dataFimField.setText("");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao criar evento: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(createButton, gbc);
        return panel;
    }

    // Adicione este método utilitário na classe view.MainGUI ou em uma classe de utilitários
    private boolean validarDatas(String dataInicio, String dataFim, JComponent parent) {
        try {
            LocalDate inicio = LocalDate.parse(dataInicio);
            LocalDate fim = LocalDate.parse(dataFim);

            if (fim.isBefore(inicio)) {
                JOptionPane.showMessageDialog(parent,
                        "Data de fim não pode ser anterior à data de início",
                        "Erro de Data", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(parent,
                    "Formato de data inválido. Use AAAA-MM-DD",
                    "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private boolean validarCamposObrigatorios(JPanel panel, JComponent... campos) {
        for (JComponent campo : campos) {
            if (campo instanceof JTextComponent && ((JTextComponent) campo).getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(panel,
                        "Todos os campos marcados com * são obrigatórios!",
                        "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                campo.requestFocusInWindow();
                return false;
            } else if (campo instanceof JComboBox && ((JComboBox<?>) campo).getSelectedItem() == null) {
                JOptionPane.showMessageDialog(panel,
                        "Todos os campos marcados com * são obrigatórios!",
                        "Campo Obrigatório", JOptionPane.WARNING_MESSAGE);
                campo.requestFocusInWindow();
                return false;
            }
        }
        return true;
    }

    private JPanel createEventListPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Lista de Eventos");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        List<Evento> eventos = eventoDAO.listarTodos();
        String[] columnNames = {"ID", "Nome", "Descrição", "Data Início", "Data Fim"};
        Object[][] data = new Object[eventos.size()][5];

        for (int i = 0; i < eventos.size(); i++) {
            Evento evento = eventos.get(i);
            data[i][0] = evento.getId();
            data[i][1] = evento.getNome();
            data[i][2] = evento.getDescricao();
            data[i][3] = evento.getDataInicio();
            data[i][4] = evento.getDataFim();
        }

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Implementar os outros métodos de criação de painéis de forma similar
    // (createEventEditPanel, createEventDeletePanel, createActivityAddPanel, etc.)

    private JPanel createEventRegistrationPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Inscrever-se em Evento");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        List<Evento> eventos = eventoDAO.listarTodos();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Evento evento : eventos) {
            listModel.addElement(evento.getId() + " - " + evento.getNome() + " (" + evento.getDataInicio() + " a " + evento.getDataFim() + ")");
        }

        JList<String> eventList = new JList<>(listModel);
        eventList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(eventList);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton registerButton = new JButton("Inscrever-se");
        registerButton.setBackground(PRIMARY_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> {
            int selectedIndex = eventList.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(frame, "Selecione um evento para se inscrever",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int eventoId = eventos.get(selectedIndex).getId();
                inscricaoDAO.inscreverEmEvento(participanteLogado.getId(), eventoId);
                JOptionPane.showMessageDialog(frame, "Inscrição realizada com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (InscricaoDuplicadaException ex) {
                JOptionPane.showMessageDialog(frame, "Você já está inscrito neste evento",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Erro ao realizar inscrição: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        buttonPanel.add(registerButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createActivityRegistrationPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Inscrever-se em Atividade");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Lista de eventos
        List<Evento> eventos = eventoDAO.listarTodos();
        DefaultComboBoxModel<Evento> eventComboModel = new DefaultComboBoxModel<>();
        for (Evento evento : eventos) {
            eventComboModel.addElement(evento);
        }

        JComboBox<Evento> eventCombo = new JComboBox<>(eventComboModel);
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Selecione o evento:"));
        selectionPanel.add(eventCombo);

        // Lista de atividades (atualizada quando seleciona um evento)
        DefaultComboBoxModel<Atividade> activityComboModel = new DefaultComboBoxModel<>();
        JComboBox<Atividade> activityCombo = new JComboBox<>(activityComboModel);
        selectionPanel.add(new JLabel("Selecione a atividade:"));
        selectionPanel.add(activityCombo);

        // Atualiza atividades quando seleciona um evento
        eventCombo.addActionListener(e -> {
            Evento selectedEvent = (Evento) eventCombo.getSelectedItem();
            if (selectedEvent != null) {
                try {
                    List<Atividade> atividades = atividadeDAO.listarPorEvento(selectedEvent.getId());
                    activityComboModel.removeAllElements();
                    for (Atividade atividade : atividades) {
                        activityComboModel.addElement(atividade);
                    }
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(panel,
                            "Erro ao carregar atividades: " + ex.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(selectionPanel, BorderLayout.NORTH);

        // Painel de informações da atividade
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel descricaoLabel = new JLabel();
        JLabel dataLabel = new JLabel();
        JLabel vagasLabel = new JLabel();
        JLabel tipoLabel = new JLabel();

        infoPanel.add(new JLabel("Descrição:"), gbc);
        infoPanel.add(descricaoLabel, gbc);
        infoPanel.add(new JLabel("Data:"), gbc);
        infoPanel.add(dataLabel, gbc);
        infoPanel.add(new JLabel("Vagas disponíveis:"), gbc);
        infoPanel.add(vagasLabel, gbc);
        infoPanel.add(new JLabel("Tipo:"), gbc);
        infoPanel.add(tipoLabel, gbc);

        // Atualiza informações quando seleciona uma atividade
        activityCombo.addActionListener(e -> {
            Atividade selected = (Atividade) activityCombo.getSelectedItem();
            if (selected != null) {
                descricaoLabel.setText(selected.getDescricao());
                dataLabel.setText(selected.getData());
                vagasLabel.setText(String.valueOf(selected.getLimiteInscritos()));
                tipoLabel.setText(selected.getTipo());
            }
        });

        panel.add(infoPanel, BorderLayout.CENTER);

        // Botão de inscrição
        JButton registerButton = new JButton("Inscrever-se");
        registerButton.setBackground(PRIMARY_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.addActionListener(e -> {
            Atividade selected = (Atividade) activityCombo.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(panel,
                        "Selecione uma atividade para se inscrever",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                LocalDate dataAtividade = LocalDate.parse(selected.getData());
                LocalDate hoje = LocalDate.now();

                if (dataAtividade.isBefore(hoje)) {
                    JOptionPane.showMessageDialog(panel,
                            "Não é possível se inscrever em atividades já realizadas",
                            "Data Inválida", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                inscricaoDAO.inscreverEmAtividade(
                        participanteLogado.getId(),
                        ((Evento) eventCombo.getSelectedItem()).getId(),
                        selected.getId()
                );
                JOptionPane.showMessageDialog(panel,
                        "Inscrição realizada com sucesso!",
                        "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                // Atualiza a lista de atividades
                Evento evento = (Evento) eventCombo.getSelectedItem();
                List<Atividade> atividades = atividadeDAO.listarPorEvento(evento.getId());
                activityComboModel.removeAllElements();
                for (Atividade atividade : atividades) {
                    activityComboModel.addElement(atividade);
                }
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Formato de data inválido. Use AAAA-MM-DD",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (InscricaoDuplicadaException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Você já está inscrito nesta atividade",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (AtividadeLotadaException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Esta atividade já está lotada",
                        "Erro", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Erro ao realizar inscrição: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createMyRegistrationsPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Minhas Inscrições");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        List<InscricaoDAO.InscricaoInfo> inscricoes = inscricaoDAO.listarPorParticipante(participanteLogado.getId());
        String[] columnNames = {"ID", "Evento", "Atividade", "Data Inscrição", "Status Pagamento", "Valor"};
        Object[][] data = new Object[inscricoes.size()][6];

        for (int i = 0; i < inscricoes.size(); i++) {
            InscricaoDAO.InscricaoInfo inscricao = inscricoes.get(i);
            data[i][0] = inscricao.getId();
            data[i][1] = inscricao.getEvento();
            data[i][2] = inscricao.getAtividade();
            data[i][3] = inscricao.getDataInscricao();
            data[i][4] = inscricao.getStatusPagamento() == null ? "PENDENTE" : inscricao.getStatusPagamento();
            data[i][5] = inscricao.getValor() != null ? inscricao.getValor() : 0.0;
        }

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPaymentRegistrationPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Registrar Pagamento");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"ID", "Evento", "Atividade", "Valor"}, 0
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 3 ? Double.class : String.class;
            }
        };

        // Obtém inscrições pendentes do participante
        List<InscricaoDAO.InscricaoInfo> inscricoes = inscricaoDAO.listarComPagamentoPendentePorParticipante(participanteLogado.getId());

        // Cria tabela com as inscrições pendentes
        String[] columnNames = {"ID", "Evento", "Atividade", "Valor"};
        Object[][] data = new Object[inscricoes.size()][4];

        for (int i = 0; i < inscricoes.size(); i++) {
            InscricaoDAO.InscricaoInfo inscricao = inscricoes.get(i);
            data[i][0] = inscricao.getId();
            data[i][1] = inscricao.getEvento();
            data[i][2] = inscricao.getAtividade();
            data[i][3] = inscricao.getValor() != null ? inscricao.getValor() : 0.0;
        }

        JTable table = new JTable(data, columnNames) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 3 ? Double.class : Object.class;
            }
        };
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Painel de informações do pagamento
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);


        JLabel tipoLabel = new JLabel("Tipo: " + participanteLogado.getTipo());
        JLabel valorCalculadoLabel = new JLabel("Valor Calculado: R$ 0,00");

        infoPanel.add(tipoLabel, gbc);
        infoPanel.add(valorCalculadoLabel, gbc);

        // Atualiza valores quando seleciona uma inscrição
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    try {


                        // Valor calculado pelo tipo de participante
                        double valorCalculado = valorInscricaoDAO.getValorPorTipo(participanteLogado.getTipo());
                        valorCalculadoLabel.setText("Valor Calculado: R$ " + String.format("%.2f", valorCalculado));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, "Erro ao obter valores: " + ex.getMessage(),
                                "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        panel.add(infoPanel, BorderLayout.NORTH);

        // Botão de registrar pagamento
        JButton payButton = new JButton("Registrar Pagamento");
        payButton.setBackground(PRIMARY_COLOR);
        payButton.setForeground(Color.WHITE);
        payButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(panel, "Selecione uma inscrição para registrar o pagamento",
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int inscricaoId = (int) table.getValueAt(selectedRow, 0);
                double valorCalculado = valorInscricaoDAO.getValorPorTipo(participanteLogado.getTipo());

                // Mostra confirmação com o valor calculado
                int confirm = JOptionPane.showConfirmDialog(panel,
                        "Confirmar pagamento de R$ " + String.format("%.2f", valorCalculado) +
                                " para esta inscrição?",
                        "Confirmar Pagamento", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    pagamentoDAO.registrarPagamento(inscricaoId, valorCalculado);
                    JOptionPane.showMessageDialog(panel,
                            "Pagamento registrado com sucesso!",
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);

                    // Atualiza a tabela
                    model.setRowCount(0);
                    List<InscricaoDAO.InscricaoInfo> updatedInscricoes =
                            inscricaoDAO.listarComPagamentoPendentePorParticipante(participanteLogado.getId());

                    for (InscricaoDAO.InscricaoInfo inscricao : updatedInscricoes) {
                        model.addRow(new Object[]{
                                inscricao.getId(),
                                inscricao.getEvento(),
                                inscricao.getAtividade(),
                                inscricao.getValor() != null ? inscricao.getValor() : 0.0
                        });
                    }
                }
            } catch (SQLException | PagamentoInvalidoException | EntidadeNaoEncontradaException ex) {
                JOptionPane.showMessageDialog(panel,
                        "Erro ao registrar pagamento: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel,
                        "Erro inesperado: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(payButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }
}