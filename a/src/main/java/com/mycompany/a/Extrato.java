package com.mycompany.a;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "extrato", urlPatterns = {"/extrato"})
public class Extrato extends HttpServlet {

    private final String URL = "jdbc:derby://localhost:1527/trabalho";
    private final String USER = "eri";
    private final String PASS = "eri";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sessao = req.getSession(false);
        if (sessao == null || sessao.getAttribute("idUsuario") == null) {
            resp.sendRedirect("login");
            return;
        }

        int idUsuario = (int) sessao.getAttribute("idUsuario");
        int idConta = 0;

        // Obter o ID da conta
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            PreparedStatement ps = con.prepareStatement("SELECT id FROM conta WHERE usuario_id = ?");
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idConta = rs.getInt("id");
            } else {
                throw new SQLException("Conta não encontrada.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Pega transações
        StringBuilder transacoesHtml = new StringBuilder();
        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            String sql = "SELECT * FROM transacao WHERE conta_origem = ? OR conta_destino = ? ORDER BY data DESC";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idConta);
            ps.setInt(2, idConta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String tipo = rs.getString("tipo");
                double valor = rs.getDouble("valor");
                Timestamp data = rs.getTimestamp("data");
                int origem = rs.getInt("conta_origem");
                int destino = rs.getInt("conta_destino");

                String cor;
                switch (tipo) {
                    case "deposito":
                        cor = "#3bb54a";
                        break;
                    case "saque":
                        cor = "#e74c3c";
                        break;
                    case "transferencia":
                        cor = "#3498db";
                        break;
                    case "investimento":
                        cor = "#9b59b6";
                        break;
                    default:
                        cor = "#999";
                }

                String descricao;
                switch (tipo) {
                    case "deposito":
                        descricao = "Depósito realizado";
                        break;
                    case "saque":
                        descricao = "Saque efetuado";
                        break;
                    case "transferencia":
                        descricao = "Transferência para conta " + destino;
                        break;
                    case "investimento":
                        descricao = "Investimento aplicado";
                        break;
                    default:
                        descricao = "Outro tipo";
                }


                transacoesHtml.append("<div class='item'>")
                    .append("<div class='faixa' style='background:").append(cor).append("'></div>")
                    .append("<div class='conteudo'>")
                    .append("<div class='valor'>R$ ").append(String.format("%.2f", valor)).append("</div>")
                    .append("<div class='desc'>").append(descricao).append("</div>")
                    .append("<div class='data'>").append(data.toString()).append("</div>")
                    .append("</div></div>");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            transacoesHtml.append("<p>Erro ao carregar transações.</p>");
        }

        // Página HTML
        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><title>Extrato</title>");
            out.println("<style>");
            out.println("body { font-family: Arial; background: #f4f4f4; padding: 20px; }");
            out.println(".container { max-width: 600px; margin: auto; }");
            out.println(".item { background: #fff; margin-bottom: 15px; display: flex; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); overflow: hidden; }");
            out.println(".faixa { width: 8px; }");
            out.println(".conteudo { padding: 15px; flex: 1; }");
            out.println(".valor { font-size: 20px; font-weight: bold; margin-bottom: 4px; }");
            out.println(".desc { color: #555; margin-bottom: 4px; }");
            out.println(".data { font-size: 12px; color: #888; }");
            out.println("a { display: inline-block; margin-top: 20px; text-decoration: none; color: #333; }");
            out.println("</style>");
            out.println("</head><body><div class='container'>");
            out.println("<h2>Extrato da Conta</h2>");
            out.println(transacoesHtml.toString());
            out.println("<a href='painel'>&larr; Voltar ao painel</a>");
            out.println("</div></body></html>");
        }
    }
}
