package com.mycompany.a;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "saldo", urlPatterns = {"/saldo"})
public class Saldo extends HttpServlet {

    String url = "jdbc:derby://localhost:1527/trabalho";
    String usuario = "eri";
    String senhaBD = "eri";

    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    HttpSession sessao = request.getSession(false);
    if (sessao == null || sessao.getAttribute("idUsuario") == null) {
        response.sendRedirect("login");
        return;
    }

    int idUsuario = (int) sessao.getAttribute("idUsuario");
    double saldo = 0;
    String numeroConta = "";

    try (Connection con = DriverManager.getConnection(url, usuario, senhaBD)) {
        String sql = "SELECT numero, saldo FROM conta WHERE usuario_id = ?";
        PreparedStatement pst = con.prepareStatement(sql);
        pst.setInt(1, idUsuario);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            numeroConta = rs.getString("numero");
            saldo = rs.getDouble("saldo");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    response.setContentType("text/html;charset=UTF-8");
    try (PrintWriter out = response.getWriter()) {
        out.println("<!DOCTYPE html><html lang='pt-br'><head>");
        out.println("<meta charset='UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Saldo – Banco Campos</title>");
        out.println("<style>");
        out.println(":root{--bg:#0b1c2c;--accent:#d4af37;--text:#fff;--muted:#cfd6e1}");
        out.println("*{box-sizing:border-box;margin:0;padding:0;font-family:'Segoe UI',Arial,sans-serif}");
        out.println("body{background:var(--bg);color:var(--text);display:flex;flex-direction:column;min-height:100vh}");
        out.println("header{display:flex;justify-content:space-between;align-items:center;padding:20px 8%;background:rgba(0,0,0,.3)}");
        out.println(".logo{font-size:1.8rem;font-weight:700;color:var(--accent)}");
        out.println(".btn-top{background:var(--accent);color:#000;padding:8px 18px;border:none;border-radius:6px;font-weight:600;cursor:pointer}");
        out.println(".card{background:#13273a;padding:40px 32px;border-radius:10px;box-shadow:0 10px 18px rgba(0,0,0,.4);width:90%;max-width:420px;margin:80px auto;text-align:center}");
        out.println(".card h2{margin-bottom:10px;color:var(--accent)}");
        out.println(".card h3{margin-bottom:8px;color:var(--muted)}");
        out.println(".saldo{font-size:28px;font-weight:bold;color:#2ecc71;margin-bottom:20px}");
        out.println("a.link{display:block;text-align:center;color:var(--muted);margin-top:20px;text-decoration:none}");
        out.println("a.link:hover{color:var(--accent)}");
        out.println("</style>");
        out.println("</head><body>");

        out.println("<header><div class='logo'>Banco Campos</div>");
        out.println("<button class='btn-top' onclick=\"location.href='painel'\">Painel</button></header>");

        out.println("<div class='card'>");
        out.println("<h2>Conta: " + numeroConta + "</h2>");
        out.println("<h3>Saldo disponível:</h3>");
        out.println("<div class='saldo'>R$ " + String.format("%.2f", saldo) + "</div>");
        out.println("<a class='link' href='painel'>&larr; Voltar ao Painel</a>");
        out.println("</div>");

        out.println("</body></html>");
    }
}
}
