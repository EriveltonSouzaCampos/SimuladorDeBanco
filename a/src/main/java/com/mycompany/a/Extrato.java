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

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html lang='pt-br'><head>");
            out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<title>Extrato – Banco Campos</title>");
            out.println("<style>");
            out.println(":root{--bg:#0b1c2c;--accent:#d4af37;--card:#13273a;--text:#fff;--muted:#cfd6e1}");
            out.println("*{box-sizing:border-box;margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif}");
            out.println("body{background:var(--bg);color:var(--text);display:flex;flex-direction:column;align-items:center;padding:40px}");
            out.println(".container{max-width:700px;width:95%;background:var(--card);border-radius:10px;padding:30px;box-shadow:0 10px 18px rgba(0,0,0,.4)}");
            out.println("h2{color:var(--accent);margin-bottom:20px;text-align:center}");
            out.println(".scroll-area{max-height:500px;overflow-y:auto;padding-right:4px}");
            out.println(".item{display:flex;background:#1e3248;margin-bottom:10px;border-radius:6px;box-shadow:0 1px 3px rgba(0,0,0,.2);overflow:hidden}");
            out.println(".faixa{width:6px}");
            out.println(".conteudo{padding:15px;flex:1}");
            out.println(".valor{font-size:18px;font-weight:bold;margin-bottom:4px;color:var(--accent)}");
            out.println(".desc{color:var(--muted);margin-bottom:3px}");
            out.println(".data{font-size:12px;color:#aaaaaa}");
            out.println("a.voltar{display:inline-block;margin-top:25px;padding:10px 20px;background:var(--accent);color:#000;text-decoration:none;border-radius:6px;font-weight:600}");
            out.println("a.voltar:hover{filter:brightness(1.1)}");
            out.println("::-webkit-scrollbar{width:8px}");
            out.println("::-webkit-scrollbar-thumb{background:#555;border-radius:10px}");
            out.println("</style></head><body>");
            out.println("<div class='container'>");
            out.println("<h2>Extrato da Conta</h2>");
            out.println("<div class='scroll-area'>");
            out.println(transacoesHtml.toString());   // blocos gerados acima
            out.println("</div>");
            out.println("<a href='painel' class='voltar'>&larr; Voltar ao Painel</a>");
            out.println("</div></body></html>");
        }
    }
}