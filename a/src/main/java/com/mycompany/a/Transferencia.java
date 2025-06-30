package com.mycompany.a;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet(name = "transferencia", urlPatterns = {"/transferencia"})
public class Transferencia extends HttpServlet {

    private final String URL  = "jdbc:derby://localhost:1527/trabalho";
    private final String USER = "eri";
    private final String PASS = "eri";

    /* ---------- TELA (GET) ---------- */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession ses = req.getSession(false);
        if (ses == null || ses.getAttribute("idUsuario") == null) {
            resp.sendRedirect("login");
            return;
        }

        int idUsuario = (int) ses.getAttribute("idUsuario");
        int idConta   = 0;
        double saldo  = 0;
        String numero = "";

        try (Connection con = DriverManager.getConnection(URL, USER, PASS)) {
            PreparedStatement ps = con.prepareStatement(
                    "SELECT id, numero, saldo FROM conta WHERE usuario_id = ?");
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                idConta = rs.getInt("id");
                numero  = rs.getString("numero");
                saldo   = rs.getDouble("saldo");
                ses.setAttribute("idConta", idConta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        resp.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = resp.getWriter()) {
            out.println("<!DOCTYPE html><html><head><title>Transferência</title>");
            out.println("<style>");
            out.println("body{font-family:Arial;background:#f0f0f0;text-align:center;padding-top:40px}");
            out.println(".card{background:#fff;display:inline-block;padding:30px;border-radius:10px;box-shadow:0 0 10px rgba(0,0,0,.1)}");
            out.println("input{padding:8px;border-radius:6px;border:1px solid #ccc;margin:5px}");
            out.println("button{padding:8px 16px;background:#3498db;color:#fff;border:none;border-radius:6px;cursor:pointer}");
            out.println(".msg{margin-top:10px;font-weight:bold}");
            out.println("</style></head><body>");
            out.println("<div class='card'>");
            out.println("<h2>Transferência</h2>");
            out.println("<p>Conta: <strong>" + numero + "</strong></p>");
            out.println("<p>Saldo atual: <strong>R$ " + String.format("%.2f", saldo) + "</strong></p>");

            String ok   = (String) ses.getAttribute("msgTransfer");
            String erro = (String) ses.getAttribute("erroTransfer");
            if (ok != null)   { out.println("<p class='msg' style='color:green'>" + ok   + "</p>");   ses.removeAttribute("msgTransfer"); }
            if (erro != null) { out.println("<p class='msg' style='color:red'>"   + erro + "</p>"); ses.removeAttribute("erroTransfer"); }

            out.println("<form method='post' action='transferencia'>");
            out.println("<input type='text'   name='contaDestino' placeholder='Conta destino' required><br>");
            out.println("<input type='number' step='0.01' name='valor'        placeholder='Valor' required><br><br>");
            out.println("<button type='submit'>Transferir</button>");
            out.println("</form>");
            out.println("<br><a href='painel'>Voltar ao painel</a>");
            out.println("</div></body></html>");
        }
    }

    /* ---------- PROCESSA (POST) ---------- */
   @Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

    HttpSession ses = req.getSession(false);
    if (ses == null || ses.getAttribute("idUsuario") == null) {
        resp.sendRedirect("login");
        return;
    }

    int idContaOrigem = (int) ses.getAttribute("idConta");
    String numDestino = req.getParameter("contaDestino");
    double valor = 0;
    String redirectURL = "transferencia";

    try { valor = Double.parseDouble(req.getParameter("valor")); }
    catch (NumberFormatException ignore) {}

    if (valor <= 0 || numDestino == null || numDestino.isBlank()) {
        ses.setAttribute("erroTransfer", "Valor ou conta inválidos.");
        resp.sendRedirect(redirectURL);
        return;
    }

    Connection con = null;
    try {
        con = DriverManager.getConnection(URL, USER, PASS);

        PreparedStatement psOrig = con.prepareStatement(
            "SELECT saldo, numero FROM conta WHERE id = ?");
        psOrig.setInt(1, idContaOrigem);
        ResultSet rsOrig = psOrig.executeQuery();
        if (!rsOrig.next()) throw new SQLException("Conta origem não encontrada.");

        double saldoOrigem = rsOrig.getDouble("saldo");
        String numOrigem   = rsOrig.getString("numero");

        if (numDestino.equals(numOrigem)) {
            ses.setAttribute("erroTransfer", "Não é possível transferir para a própria conta.");
            return;
        }
        if (valor > saldoOrigem) {
            ses.setAttribute("erroTransfer", "Saldo insuficiente.");
            return;
        }

        PreparedStatement psDest = con.prepareStatement(
            "SELECT id FROM conta WHERE numero = ?");
        psDest.setString(1, numDestino);
        ResultSet rsDest = psDest.executeQuery();
        if (!rsDest.next()) {
            ses.setAttribute("erroTransfer", "Conta destino não encontrada.");
            return;
        }
        int idContaDestino = rsDest.getInt("id");

        con.setAutoCommit(false);

        PreparedStatement deb = con.prepareStatement(
            "UPDATE conta SET saldo = saldo - ? WHERE id = ?");
        deb.setDouble(1, valor);
        deb.setInt(2, idContaOrigem);
        deb.executeUpdate();

        PreparedStatement cred = con.prepareStatement(
            "UPDATE conta SET saldo = saldo + ? WHERE id = ?");
        cred.setDouble(1, valor);
        cred.setInt(2, idContaDestino);
        cred.executeUpdate();

        PreparedStatement pt = con.prepareStatement(
            "INSERT INTO transacao (conta_origem, conta_destino, tipo, valor) "
          + "VALUES (?, ?, 'transferencia', ?)");
        pt.setInt(1, idContaOrigem);
        pt.setInt(2, idContaDestino);
        pt.setDouble(3, valor);
        pt.executeUpdate();

        con.commit();
        ses.setAttribute("msgTransfer", "Transferência realizada com sucesso!");

    } catch (SQLException e) {
        if (con != null) try { con.rollback(); } catch (Exception ig) {}
        e.printStackTrace();
        ses.setAttribute("erroTransfer", "Erro ao transferir: " + e.getMessage());
    } finally {
        if (con != null) try { con.close(); } catch (Exception ig) {}
        resp.sendRedirect(redirectURL);
    }
}

}
