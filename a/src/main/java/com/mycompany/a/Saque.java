package com.mycompany.a;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "saque", urlPatterns = {"/saque"})
public class Saque extends HttpServlet {

    private final String URL   = "jdbc:derby://localhost:1527/trabalho";
    private final String USER  = "eri";
    private final String PASS  = "eri";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sessao = req.getSession(false);
        if (sessao == null || sessao.getAttribute("idUsuario") == null) {
            resp.sendRedirect("login");
            return;
        }

        int idUsuario = (int) sessao.getAttribute("idUsuario");
        double saldo  = 0;
        String conta  = "";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            PreparedStatement ps = con.prepareStatement(
                "SELECT id, numero, saldo FROM conta WHERE usuario_id = ?"
            );
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                conta = rs.getString("numero");
                saldo = rs.getDouble("saldo");
                sessao.setAttribute("idConta", rs.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Saque</title>");
            out.println("<style>");
            out.println("body{font-family:Arial;background:#f4f4f4;text-align:center;padding-top:40px}");
            out.println(".card{background:#fff;border-radius:10px;box-shadow:0 0 10px rgba(0,0,0,.1);display:inline-block;padding:30px}");
            out.println(".valor-btn{margin:4px;padding:8px 16px;border:1px solid #007bff;border-radius:6px;background:#e9f1ff;cursor:pointer}");
            out.println("input[type=number]{padding:6px 8px;width:120px;border-radius:6px;border:1px solid #ccc}");
            out.println("</style>");
            out.println("<script>");
            out.println("function setValor(v){document.getElementById('valor').value=v;}");
            out.println("</script>");
            out.println("</head><body>");
            out.println("<div class='card'>");
            out.println("<h2>Conta "+conta+"</h2>");
            out.println("<p>Saldo atual: <strong>R$ "+String.format("%.2f",saldo)+"</strong></p>");

            String msg = (String) sessao.getAttribute("msgSaque");
            if (msg != null){ out.println("<p style='color:green'>"+msg+"</p>"); sessao.removeAttribute("msgSaque"); }

            String erro = (String) sessao.getAttribute("erroSaque");
            if (erro != null){ out.println("<p style='color:red'>"+erro+"</p>"); sessao.removeAttribute("erroSaque"); }

            out.println("<form method='post' action='saque'>");
            out.println("<input type='number' step='0.01' name='valor' id='valor' placeholder='Valor'>");
            out.println("<br><br>");
            out.println("<button type='button' class='valor-btn' onclick='setValor(10)'>10</button>");
            out.println("<button type='button' class='valor-btn' onclick='setValor(50)'>50</button>");
            out.println("<button type='button' class='valor-btn' onclick='setValor(100)'>100</button>");
            out.println("<button type='button' class='valor-btn' onclick='setValor(1000)'>1000</button>");
            out.println("<br><br>");
            out.println("<input type='submit' value='Sacar'>");
            out.println("</form>");

            out.println("<br><a href='painel'>Voltar ao Painel</a>");
            out.println("</div></body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession sessao = req.getSession(false);
        if (sessao == null || sessao.getAttribute("idUsuario") == null) {
            resp.sendRedirect("login");
            return;
        }

        double valor = 0;
        try { valor = Double.parseDouble(req.getParameter("valor")); }
        catch (NumberFormatException ignored){}

        if (valor <= 0) {
            sessao.setAttribute("erroSaque", "Valor inválido.");
            resp.sendRedirect("saque");
            return;
        }

        int idConta = (int) sessao.getAttribute("idConta");

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            con.setAutoCommit(false);

            // Consulta saldo atual
            PreparedStatement psSaldo = con.prepareStatement(
                "SELECT saldo FROM conta WHERE id = ?"
            );
            psSaldo.setInt(1, idConta);
            ResultSet rs = psSaldo.executeQuery();
            if (!rs.next()) throw new SQLException("Conta não encontrada.");

            double saldoAtual = rs.getDouble("saldo");

            if (valor > saldoAtual) {
                sessao.setAttribute("erroSaque", "Saldo insuficiente.");
                resp.sendRedirect("saque");
                return;
            }

            // 1. Atualiza saldo
            PreparedStatement ps1 = con.prepareStatement(
                "UPDATE conta SET saldo = saldo - ? WHERE id = ?"
            );
            ps1.setDouble(1, valor);
            ps1.setInt(2, idConta);
            ps1.executeUpdate();

            // 2. Registra transação
            PreparedStatement ps2 = con.prepareStatement(
                "INSERT INTO transacao (conta_origem, tipo, valor) VALUES (?, 'saque', ?)"
            );
            ps2.setInt(1, idConta);
            ps2.setDouble(2, valor);
            ps2.executeUpdate();

            con.commit();
            sessao.setAttribute("msgSaque", "Saque de R$ " + String.format("%.2f", valor) + " realizado com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            sessao.setAttribute("erroSaque", "Erro ao sacar: " + e.getMessage());
        }
        resp.sendRedirect("saque");
    }
}
