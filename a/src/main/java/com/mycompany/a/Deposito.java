package com.mycompany.a;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "deposito", urlPatterns = {"/deposito"})
public class Deposito extends HttpServlet {

    private final String URL  = "jdbc:derby://localhost:1527/trabalho";
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
    double saldo = 0;
    String conta = "";

    try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
        PreparedStatement ps = con.prepareStatement(
            "SELECT id, numero, saldo FROM conta WHERE usuario_id = ?");
        ps.setInt(1, idUsuario);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            conta = rs.getString("numero");
            saldo = rs.getDouble("saldo");
            sessao.setAttribute("idConta", rs.getInt("id"));
        }
    } catch (SQLException e) { e.printStackTrace(); }

    resp.setContentType("text/html;charset=UTF-8");
    PrintWriter out = resp.getWriter();

    out.println("<!DOCTYPE html><html lang='pt-br'><head>");
    out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
    out.println("<title>Depósito – Banco Campos</title>");
    out.println("<style>");
    out.println(":root{--bg:#0b1c2c;--accent:#d4af37;--text:#fff;--muted:#cfd6e1;--btn:#007bff}");
    out.println("*{box-sizing:border-box;margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif}");
    out.println("body{background:var(--bg);color:var(--text);display:flex;flex-direction:column;min-height:100vh}");
    out.println("header{display:flex;justify-content:space-between;align-items:center;padding:20px 8%;background:rgba(0,0,0,.3)}");
    out.println(".logo{font-size:1.8rem;font-weight:700;color:var(--accent)}");
    out.println(".btn-top{background:var(--accent);color:#000;padding:8px 18px;border:none;border-radius:6px;font-weight:600;cursor:pointer}");
    out.println(".card{background:#13273a;padding:40px 32px;border-radius:10px;box-shadow:0 10px 18px rgba(0,0,0,.4);width:90%;max-width:420px;margin:80px auto}");
    out.println(".card h2{margin-bottom:6px;color:var(--accent)}");
    out.println(".card p{margin-bottom:20px;color:var(--muted)}");
    out.println("input{width:100%;padding:12px;margin:8px 0;background:#0f2236;border:1px solid #334861;border-radius:6px;color:var(--text)}");
    out.println("input::placeholder{color:var(--muted)}");
    out.println(".valor-btn{margin:4px;padding:8px 16px;border:1px solid var(--btn);border-radius:6px;background:#e9f1ff;color:#000;cursor:pointer}");
    out.println(".main-btn{width:100%;margin-top:16px;background:var(--btn);color:#fff;border:none;border-radius:6px;padding:12px;font-weight:600;cursor:pointer}");
    out.println(".main-btn:hover{filter:brightness(1.1)}");
    out.println("a.link{display:block;text-align:center;color:var(--muted);margin-top:20px;text-decoration:none}");
    out.println("a.link:hover{color:var(--accent)}");
    out.println("</style>");
    out.println("<script>function setValor(v){document.getElementById('valor').value=v;}</script>");
    out.println("</head><body>");

    // Header
    out.println("<header><div class='logo'>Banco Campos</div>");
    out.println("<button class='btn-top' onclick=\"location.href='painel'\">Painel</button></header>");

    // Card
    out.println("<div class='card'>");
    out.println("<h2>Conta " + conta + "</h2>");
    out.println("<p>Saldo atual: <strong>R$ " + String.format("%.2f", saldo) + "</strong></p>");

    String msg = (String) sessao.getAttribute("msgDeposito");
    if (msg != null) {
        boolean sucesso = msg.toLowerCase().contains("sucesso");
        String cor = sucesso ? "#3399ff" : "#e74c3c";
        out.println("<p style='color:" + cor + ";font-weight:600'>" + msg + "</p>");
        sessao.removeAttribute("msgDeposito");
    }

    out.println("<form method='post' action='deposito'>");
    out.println("<input type='number' step='0.01' name='valor' id='valor' placeholder='Digite o valor'>");
    out.println("<div>");
    out.println("<button type='button' class='valor-btn' onclick='setValor(10)'>10</button>");
    out.println("<button type='button' class='valor-btn' onclick='setValor(50)'>50</button>");
    out.println("<button type='button' class='valor-btn' onclick='setValor(100)'>100</button>");
    out.println("<button type='button' class='valor-btn' onclick='setValor(1000)'>1000</button>");
    out.println("</div>");
    out.println("<button class='main-btn' type='submit'>Depositar</button>");
    out.println("</form>");
    out.println("<a class='link' href='painel'>&larr; Voltar ao Painel</a>");
    out.println("</div>");

    out.println("</body></html>");
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
            sessao.setAttribute("msgDeposito", "Valor inválido.");
            resp.sendRedirect("deposito");
            return;
        }

        int idConta = (int) sessao.getAttribute("idConta");

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            con.setAutoCommit(false);

            // 1. Atualiza saldo
            PreparedStatement ps1 = con.prepareStatement(
                "UPDATE conta SET saldo = saldo + ? WHERE id = ?"
            );
            ps1.setDouble(1, valor);
            ps1.setInt(2, idConta);
            ps1.executeUpdate();

            // 2. Registra transação
            PreparedStatement ps2 = con.prepareStatement(
                "INSERT INTO transacao (conta_destino, tipo, valor) VALUES (?, 'deposito', ?)"
            );
            ps2.setInt(1, idConta);
            ps2.setDouble(2, valor);
            ps2.executeUpdate();

            con.commit();
            sessao.setAttribute("msgDeposito", "Depósito realizado com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            sessao.setAttribute("msgDeposito", "Erro ao depositar: " + e.getMessage());
        }

        resp.sendRedirect("deposito");
    }
}
