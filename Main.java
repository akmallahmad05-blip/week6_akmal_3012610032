
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/** Titik masuk aplikasi Portal Mahasiswa Mini. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // pakai tampilan bawaan jika gagal
            }
            new MainFrame().setVisible(true);
        });
    }
}

// ============================================================
/**
 * Jendela utama. Berisi menu File > Keluar dan Bantuan > Tentang (Tugas 2 no. 5)
 * serta satu tab untuk setiap modul tugas.
 */
class MainFrame extends JFrame {

    private final EditorTeksPanel editor = new EditorTeksPanel();

    public MainFrame() {
        super("Portal Mahasiswa Mini");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                keluar();
            }
        });

        setJMenuBar(buatMenuBar());

        JTabbedPane tab = new JTabbedPane();
        tab.addTab("T1 Registrasi", new FormRegistrasiPanel());
        tab.addTab("T1 Editor Teks", editor);
        tab.addTab("T1 Data Mahasiswa", new DataMahasiswaPanel());
        tab.addTab("T2 Layout", new LayoutDemoPanel());
        tab.addTab("T2 Login", new LoginPanel());
        tab.addTab("T2 Belanja", new DaftarBelanjaPanel());
        tab.addTab("T2 Jam Digital", new JamDigitalPanel());
        add(tab);

        setSize(940, 660);
        setLocationRelativeTo(null);
    }

    private JMenuBar buatMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuFile.setMnemonic(KeyEvent.VK_F);
        JMenuItem miKeluar = new JMenuItem("Keluar");
        miKeluar.setMnemonic(KeyEvent.VK_K);
        miKeluar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        miKeluar.addActionListener(e -> keluar());
        menuFile.add(miKeluar);

        JMenu menuBantuan = new JMenu("Bantuan");
        menuBantuan.setMnemonic(KeyEvent.VK_B);
        JMenuItem miTentang = new JMenuItem("Tentang");
        miTentang.setMnemonic(KeyEvent.VK_T);
        miTentang.addActionListener(e -> tentang());
        menuBantuan.add(miTentang);

        bar.add(menuFile);
        bar.add(menuBantuan);
        return bar;
    }

    /** Keluar dari aplikasi setelah konfirmasi (dan cek perubahan di editor teks). */
    public void keluar() {
        if (!editor.bolehLanjut()) {
            return;
        }
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Yakin ingin keluar dari aplikasi?", "Keluar",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (pilihan == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }

    private void tentang() {
        JOptionPane.showMessageDialog(this,
                "Portal Mahasiswa Mini  v1.0\n\n"
                        + "Aplikasi Java Swing untuk latihan:\n"
                        + "GridBagLayout, FlowLayout, GridLayout, JTable, JList,\n"
                        + "JFileChooser, menu, toolbar, dan javax.swing.Timer.",
                "Tentang", JOptionPane.INFORMATION_MESSAGE);
    }
}

// ============================================================
/**
 * Tugas 1 - Form Registrasi.
 * GridBagLayout: nama, email, sandi, gender, prodi, hobi (CheckBox).
 * Tombol Daftar menampilkan ringkasan di dialog.
 */
class FormRegistrasiPanel extends JPanel {

    private final JTextField txtNama = new JTextField(24);
    private final JTextField txtEmail = new JTextField(24);
    private final JPasswordField txtSandi = new JPasswordField(24);
    private final JRadioButton rbLaki = new JRadioButton("Laki-laki");
    private final JRadioButton rbPerempuan = new JRadioButton("Perempuan");
    private final JComboBox<String> cbProdi = new JComboBox<>(new String[]{
            "Informatika", "Sistem Informasi", "Teknik Elektro", "Manajemen", "Akuntansi"});
    private final JCheckBox[] hobi = {
            new JCheckBox("Membaca"), new JCheckBox("Olahraga"), new JCheckBox("Musik"),
            new JCheckBox("Gaming"), new JCheckBox("Coding")};

    public FormRegistrasiPanel() {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        ButtonGroup grupGender = new ButtonGroup();
        grupGender.add(rbLaki);
        grupGender.add(rbPerempuan);

        JPanel panelGender = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelGender.add(rbLaki);
        panelGender.add(rbPerempuan);

        JPanel panelHobi = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        for (JCheckBox cb : hobi) {
            panelHobi.add(cb);
        }

        JButton btnDaftar = new JButton("Daftar");
        JButton btnReset = new JButton("Reset");
        btnDaftar.addActionListener(e -> daftar());
        btnReset.addActionListener(e -> reset());
        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panelTombol.add(btnReset);
        panelTombol.add(btnDaftar);

        JLabel judul = new JLabel("Form Registrasi Mahasiswa");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 20f));

        int baris = 0;
        letak(judul, 0, baris++, 2, GridBagConstraints.CENTER);
        tambahBaris("Nama", txtNama, baris++);
        tambahBaris("Email", txtEmail, baris++);
        tambahBaris("Sandi", txtSandi, baris++);
        tambahBaris("Gender", panelGender, baris++);
        tambahBaris("Program Studi", cbProdi, baris++);
        tambahBaris("Hobi", panelHobi, baris++);
        letak(panelTombol, 0, baris, 2, GridBagConstraints.EAST);
    }

    private void tambahBaris(String label, Component isian, int baris) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = baris;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(6, 6, 6, 12);
        add(new JLabel(label), g);

        g = new GridBagConstraints();
        g.gridx = 1;
        g.gridy = baris;
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        g.insets = new Insets(6, 0, 6, 6);
        add(isian, g);
    }

    private void letak(Component c, int x, int y, int lebar, int anchor) {
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = x;
        g.gridy = y;
        g.gridwidth = lebar;
        g.anchor = anchor;
        g.fill = GridBagConstraints.HORIZONTAL;
        g.insets = new Insets(10, 6, 10, 6);
        add(c, g);
    }

    private void daftar() {
        String nama = txtNama.getText().trim();
        String email = txtEmail.getText().trim();
        String sandi = new String(txtSandi.getPassword());

        if (nama.isEmpty() || email.isEmpty() || sandi.isEmpty()) {
            peringatan("Nama, email, dan sandi wajib diisi.");
            return;
        }
        if (!email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$")) {
            peringatan("Format email tidak valid.");
            return;
        }
        if (sandi.length() < 6) {
            peringatan("Sandi minimal 6 karakter.");
            return;
        }
        if (!rbLaki.isSelected() && !rbPerempuan.isSelected()) {
            peringatan("Pilih gender terlebih dahulu.");
            return;
        }

        List<String> terpilih = new ArrayList<>();
        for (JCheckBox cb : hobi) {
            if (cb.isSelected()) {
                terpilih.add(cb.getText());
            }
        }

        String ringkasan = "Nama          : " + nama
                + "\nEmail          : " + email
                + "\nSandi          : " + "*".repeat(sandi.length())
                + "\nGender        : " + (rbLaki.isSelected() ? "Laki-laki" : "Perempuan")
                + "\nProgram Studi : " + cbProdi.getSelectedItem()
                + "\nHobi            : " + (terpilih.isEmpty() ? "-" : String.join(", ", terpilih));

        JOptionPane.showMessageDialog(this, ringkasan, "Ringkasan Pendaftaran",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void reset() {
        txtNama.setText("");
        txtEmail.setText("");
        txtSandi.setText("");
        rbLaki.setSelected(false);
        rbPerempuan.setSelected(false);
        // ButtonGroup tetap menyimpan pilihan lama sampai di-clear
        rbLaki.getModel().getGroup().clearSelection();
        cbProdi.setSelectedIndex(0);
        for (JCheckBox cb : hobi) {
            cb.setSelected(false);
        }
        txtNama.requestFocusInWindow();
    }

    private void peringatan(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Periksa Isian", JOptionPane.WARNING_MESSAGE);
    }
}

// ============================================================
/**
 * Tugas 1 - Editor Teks.
 * JTextArea dalam JScrollPane, menu File (Baru, Buka, Simpan, Keluar) dengan JFileChooser,
 * menu Edit (Potong, Salin, Tempel), dan toolbar.
 */
class EditorTeksPanel extends JPanel {

    private final JTextArea area = new JTextArea();
    private final JLabel status = new JLabel(" Dokumen baru");
    private final JFileChooser pemilihFile = new JFileChooser();
    private File fileSaatIni = null;
    private boolean berubah = false;

    private final Action aksiBaru = new AbstractAction("Baru") {
        @Override
        public void actionPerformed(ActionEvent e) {
            baru();
        }
    };
    private final Action aksiBuka = new AbstractAction("Buka...") {
        @Override
        public void actionPerformed(ActionEvent e) {
            buka();
        }
    };
    private final Action aksiSimpan = new AbstractAction("Simpan") {
        @Override
        public void actionPerformed(ActionEvent e) {
            simpan();
        }
    };
    private final Action aksiKeluar = new AbstractAction("Keluar") {
        @Override
        public void actionPerformed(ActionEvent e) {
            keluar();
        }
    };
    private final Action aksiPotong = new AbstractAction("Potong") {
        @Override
        public void actionPerformed(ActionEvent e) {
            area.cut();
            area.requestFocusInWindow();
        }
    };
    private final Action aksiSalin = new AbstractAction("Salin") {
        @Override
        public void actionPerformed(ActionEvent e) {
            area.copy();
            area.requestFocusInWindow();
        }
    };
    private final Action aksiTempel = new AbstractAction("Tempel") {
        @Override
        public void actionPerformed(ActionEvent e) {
            area.paste();
            area.requestFocusInWindow();
        }
    };

    public EditorTeksPanel() {
        super(new BorderLayout());

        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMargin(new Insets(8, 8, 8, 8));
        area.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                tandaiBerubah();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                tandaiBerubah();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                tandaiBerubah();
            }
        });

        pemilihFile.setFileFilter(new FileNameExtensionFilter("Berkas teks (*.txt)", "txt"));

        aksiBaru.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
        aksiBuka.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        aksiSimpan.putValue(Action.ACCELERATOR_KEY,
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));

        JPanel atas = new JPanel(new BorderLayout());
        atas.add(buatMenuBar(), BorderLayout.NORTH);
        atas.add(buatToolBar(), BorderLayout.CENTER);

        add(atas, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);
    }

    private JMenuBar buatMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu file = new JMenu("File");
        file.setMnemonic(KeyEvent.VK_F);
        file.add(aksiBaru);
        file.add(aksiBuka);
        file.add(aksiSimpan);
        file.addSeparator();
        file.add(aksiKeluar);

        JMenu edit = new JMenu("Edit");
        edit.setMnemonic(KeyEvent.VK_E);
        edit.add(aksiPotong);
        edit.add(aksiSalin);
        edit.add(aksiTempel);

        bar.add(file);
        bar.add(edit);
        return bar;
    }

    private JToolBar buatToolBar() {
        JToolBar tb = new JToolBar();
        tb.setFloatable(false);
        for (Action a : new Action[]{aksiBaru, aksiBuka, aksiSimpan}) {
            tb.add(tombolToolbar(a));
        }
        tb.addSeparator();
        for (Action a : new Action[]{aksiPotong, aksiSalin, aksiTempel}) {
            tb.add(tombolToolbar(a));
        }
        return tb;
    }

    private JButton tombolToolbar(Action a) {
        JButton b = new JButton(a);
        b.setFocusable(false);
        return b;
    }

    // ---------- Operasi berkas ----------

    private void baru() {
        if (!bolehLanjut()) {
            return;
        }
        area.setText("");
        fileSaatIni = null;
        berubah = false;
        perbaruiStatus();
    }

    private void buka() {
        if (!bolehLanjut()) {
            return;
        }
        if (pemilihFile.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File f = pemilihFile.getSelectedFile();
        try {
            String isi = new String(Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            area.setText(isi);
            area.setCaretPosition(0);
            fileSaatIni = f;
            berubah = false;
            perbaruiStatus();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal membuka berkas:\n" + ex.getMessage(),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** @return true jika berkas berhasil disimpan. */
    private boolean simpan() {
        if (fileSaatIni == null) {
            pemilihFile.setSelectedFile(new File("tanpa_judul.txt"));
            if (pemilihFile.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            File f = pemilihFile.getSelectedFile();
            if (!f.getName().contains(".")) {
                f = new File(f.getParentFile(), f.getName() + ".txt");
            }
            if (f.exists() && JOptionPane.showConfirmDialog(this,
                    f.getName() + " sudah ada. Timpa?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return false;
            }
            fileSaatIni = f;
        }
        try {
            Files.write(fileSaatIni.toPath(), area.getText().getBytes(StandardCharsets.UTF_8));
            berubah = false;
            perbaruiStatus();
            return true;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan berkas:\n" + ex.getMessage(),
                    "Kesalahan", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void keluar() {
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof MainFrame) {
            ((MainFrame) w).keluar();
        }
    }

    /**
     * Menanyakan apakah perubahan yang belum disimpan boleh dibuang.
     * @return true jika aman untuk melanjutkan.
     */
    public boolean bolehLanjut() {
        if (!berubah) {
            return true;
        }
        int p = JOptionPane.showConfirmDialog(this,
                "Ada perubahan yang belum disimpan. Simpan dulu?", "Editor Teks",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
        if (p == JOptionPane.YES_OPTION) {
            return simpan();
        }
        return p == JOptionPane.NO_OPTION;
    }

    private void tandaiBerubah() {
        berubah = true;
        perbaruiStatus();
    }

    private void perbaruiStatus() {
        String nama = (fileSaatIni == null) ? "Dokumen baru" : fileSaatIni.getName();
        status.setText(" " + nama + (berubah ? " *" : ""));
    }
}

// ============================================================
/**
 * Tugas 1 - Data Mahasiswa (pengembangan mini project).
 * Menambahkan kolom IPK dan tombol "Ubah data terpilih".
 */
class DataMahasiswaPanel extends JPanel {

    private static final int KOL_NIM = 0;
    private static final int KOL_NAMA = 1;
    private static final int KOL_PRODI = 2;
    private static final int KOL_IPK = 3;

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"NIM", "Nama", "Program Studi", "IPK"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return column == KOL_IPK ? Double.class : String.class;
        }
    };

    private final JTable tabel = new JTable(model);
    private final JTextField txtNim = new JTextField(10);
    private final JTextField txtNama = new JTextField(16);
    private final JTextField txtIpk = new JTextField(5);
    private final JComboBox<String> cbProdi = new JComboBox<>(new String[]{
            "Informatika", "Sistem Informasi", "Teknik Elektro", "Manajemen", "Akuntansi"});
    private final JLabel lblRingkasan = new JLabel(" ");

    public DataMahasiswaPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.setAutoCreateRowSorter(true);
        tabel.setRowHeight(24);
        tabel.setDefaultRenderer(Double.class, new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(RIGHT);
            }

            @Override
            protected void setValue(Object value) {
                setText(value instanceof Double
                        ? String.format(Locale.US, "%.2f", (Double) value) : "");
            }
        });
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariBaris();
            }
        });

        // data contoh
        model.addRow(new Object[]{"2024001", "Andi Pratama", "Informatika", 3.75});
        model.addRow(new Object[]{"2024002", "Siti Aminah", "Sistem Informasi", 3.60});
        model.addRow(new Object[]{"2024003", "Budi Santoso", "Teknik Elektro", 3.20});

        JPanel formIsian = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        formIsian.add(new JLabel("NIM"));
        formIsian.add(txtNim);
        formIsian.add(new JLabel("Nama"));
        formIsian.add(txtNama);
        formIsian.add(new JLabel("Prodi"));
        formIsian.add(cbProdi);
        formIsian.add(new JLabel("IPK"));
        formIsian.add(txtIpk);

        JButton btnTambah = new JButton("Tambah");
        JButton btnUbah = new JButton("Ubah data terpilih");
        JButton btnHapus = new JButton("Hapus terpilih");
        JButton btnBersih = new JButton("Bersihkan");
        btnTambah.addActionListener(e -> tambah());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersihkan());

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        tombol.add(btnTambah);
        tombol.add(btnUbah);
        tombol.add(btnHapus);
        tombol.add(btnBersih);

        JPanel selatan = new JPanel(new BorderLayout());
        selatan.setBorder(BorderFactory.createTitledBorder("Input Data"));
        selatan.add(formIsian, BorderLayout.NORTH);
        selatan.add(tombol, BorderLayout.CENTER);
        selatan.add(lblRingkasan, BorderLayout.SOUTH);

        add(new JLabel("Data Mahasiswa"), BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);
        add(selatan, BorderLayout.SOUTH);

        perbaruiRingkasan();
    }

    // ---------- Aksi ----------

    private void tambah() {
        Object[] baris = bacaForm(-1);
        if (baris == null) {
            return;
        }
        model.addRow(baris);
        bersihkan();
        perbaruiRingkasan();
    }

    private void ubah() {
        int viewRow = tabel.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih satu baris pada tabel terlebih dahulu.",
                    "Ubah Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = tabel.convertRowIndexToModel(viewRow);
        Object[] baris = bacaForm(modelRow);
        if (baris == null) {
            return;
        }
        for (int k = 0; k < baris.length; k++) {
            model.setValueAt(baris[k], modelRow, k);
        }
        perbaruiRingkasan();
        JOptionPane.showMessageDialog(this, "Data berhasil diubah.", "Ubah Data",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void hapus() {
        int viewRow = tabel.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih satu baris pada tabel terlebih dahulu.",
                    "Hapus Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = tabel.convertRowIndexToModel(viewRow);
        int p = JOptionPane.showConfirmDialog(this,
                "Hapus data " + model.getValueAt(modelRow, KOL_NAMA) + "?", "Hapus Data",
                JOptionPane.YES_NO_OPTION);
        if (p == JOptionPane.YES_OPTION) {
            model.removeRow(modelRow);
            bersihkan();
            perbaruiRingkasan();
        }
    }

    private void bersihkan() {
        tabel.clearSelection();
        txtNim.setText("");
        txtNama.setText("");
        txtIpk.setText("");
        cbProdi.setSelectedIndex(0);
        txtNim.requestFocusInWindow();
    }

    // ---------- Pembantu ----------

    private void isiFormDariBaris() {
        int viewRow = tabel.getSelectedRow();
        if (viewRow < 0) {
            return;
        }
        int r = tabel.convertRowIndexToModel(viewRow);
        txtNim.setText(String.valueOf(model.getValueAt(r, KOL_NIM)));
        txtNama.setText(String.valueOf(model.getValueAt(r, KOL_NAMA)));
        cbProdi.setSelectedItem(model.getValueAt(r, KOL_PRODI));
        txtIpk.setText(String.format(Locale.US, "%.2f", (Double) model.getValueAt(r, KOL_IPK)));
    }

    /**
     * Membaca dan memvalidasi isian form.
     * @param barisDiabaikan indeks baris model yang boleh memakai NIM yang sama (saat ubah), atau -1.
     * @return array satu baris, atau null jika tidak valid.
     */
    private Object[] bacaForm(int barisDiabaikan) {
        String nim = txtNim.getText().trim();
        String nama = txtNama.getText().trim();
        if (nim.isEmpty() || nama.isEmpty() || txtIpk.getText().trim().isEmpty()) {
            salah("NIM, nama, dan IPK wajib diisi.");
            return null;
        }
        double ipk;
        try {
            ipk = Double.parseDouble(txtIpk.getText().trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            salah("IPK harus berupa angka, contoh: 3.75");
            return null;
        }
        if (ipk < 0.0 || ipk > 4.0) {
            salah("IPK harus berada di antara 0.00 dan 4.00.");
            return null;
        }
        ipk = Math.round(ipk * 100.0) / 100.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            if (i != barisDiabaikan && nim.equals(model.getValueAt(i, KOL_NIM))) {
                salah("NIM " + nim + " sudah terdaftar.");
                return null;
            }
        }
        return new Object[]{nim, nama, cbProdi.getSelectedItem(), ipk};
    }

    private void perbaruiRingkasan() {
        int n = model.getRowCount();
        double total = 0;
        for (int i = 0; i < n; i++) {
            total += (Double) model.getValueAt(i, KOL_IPK);
        }
        String rata = (n == 0) ? "-" : String.format(Locale.US, "%.2f", total / n);
        lblRingkasan.setText("  Jumlah mahasiswa: " + n + "   |   Rata-rata IPK: " + rata);
    }

    private void salah(String pesan) {
        JOptionPane.showMessageDialog(this, pesan, "Periksa Isian", JOptionPane.WARNING_MESSAGE);
    }
}

// ============================================================
/**
 * Tugas 2 no. 1 - Jendela berisi tiga tombol dengan FlowLayout,
 * lalu diubah ke GridLayout 1 x 3 (dan bisa dikembalikan).
 */
class LayoutDemoPanel extends JPanel {

    private final JPanel wadah = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
    private final JLabel lblInfo = new JLabel("Layout saat ini: FlowLayout", SwingConstants.CENTER);
    private final JButton btnUbah = new JButton("Ubah ke GridLayout 1 x 3");
    private boolean modeFlow = true;

    public LayoutDemoPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        wadah.setBorder(BorderFactory.createTitledBorder("Area Tombol"));
        for (int i = 1; i <= 3; i++) {
            final int nomor = i;
            JButton b = new JButton("Tombol " + nomor);
            b.addActionListener(e -> JOptionPane.showMessageDialog(this,
                    "Kamu menekan Tombol " + nomor));
            wadah.add(b);
        }

        btnUbah.addActionListener(e -> gantiLayout());

        JPanel bawah = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bawah.add(btnUbah);

        add(lblInfo, BorderLayout.NORTH);
        add(wadah, BorderLayout.CENTER);
        add(bawah, BorderLayout.SOUTH);
    }

    private void gantiLayout() {
        modeFlow = !modeFlow;
        if (modeFlow) {
            wadah.setLayout(new FlowLayout(FlowLayout.CENTER, 12, 12));
            lblInfo.setText("Layout saat ini: FlowLayout");
            btnUbah.setText("Ubah ke GridLayout 1 x 3");
        } else {
            wadah.setLayout(new GridLayout(1, 3, 12, 12));
            lblInfo.setText("Layout saat ini: GridLayout 1 x 3");
            btnUbah.setText("Kembali ke FlowLayout");
        }
        wadah.revalidate();
        wadah.repaint();
    }
}

// ============================================================
/**
 * Tugas 2 no. 2 - Form login (username, password, tombol Masuk) dengan GridBagLayout.
 * Menampilkan pesan bila ada isian yang kosong.
 */
class LoginPanel extends JPanel {

    // akun contoh untuk demo
    private static final String USER_BENAR = "admin";
    private static final String SANDI_BENAR = "12345";

    private final JTextField txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);

    public LoginPanel() {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel judul = new JLabel("Login");
        judul.setFont(judul.getFont().deriveFont(Font.BOLD, 22f));
        JButton btnMasuk = new JButton("Masuk");
        JLabel petunjuk = new JLabel("Akun demo: admin / 12345");
        petunjuk.setFont(petunjuk.getFont().deriveFont(Font.ITALIC, 11f));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);

        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        add(judul, g);

        g.gridwidth = 1;
        g.gridy = 1;
        g.anchor = GridBagConstraints.WEST;
        add(new JLabel("Username"), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        add(txtUsername, g);

        g.gridx = 0;
        g.gridy = 2;
        g.fill = GridBagConstraints.NONE;
        add(new JLabel("Password"), g);
        g.gridx = 1;
        g.fill = GridBagConstraints.HORIZONTAL;
        add(txtPassword, g);

        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 2;
        g.fill = GridBagConstraints.HORIZONTAL;
        add(btnMasuk, g);

        g.gridy = 4;
        g.fill = GridBagConstraints.NONE;
        add(petunjuk, g);

        btnMasuk.addActionListener(e -> masuk());
        txtUsername.addActionListener(e -> txtPassword.requestFocusInWindow());
        txtPassword.addActionListener(e -> masuk());
    }

    private void masuk() {
        String user = txtUsername.getText().trim();
        String sandi = new String(txtPassword.getPassword());

        if (user.isEmpty() || sandi.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username dan password tidak boleh kosong!", "Login Gagal",
                    JOptionPane.WARNING_MESSAGE);
            (user.isEmpty() ? txtUsername : txtPassword).requestFocusInWindow();
            return;
        }
        if (USER_BENAR.equals(user) && SANDI_BENAR.equals(sandi)) {
            JOptionPane.showMessageDialog(this, "Login berhasil. Selamat datang, " + user + "!",
                    "Login", JOptionPane.INFORMATION_MESSAGE);
            txtPassword.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Username atau password salah.",
                    "Login Gagal", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocusInWindow();
        }
    }
}

// ============================================================
/**
 * Tugas 2 no. 3 - Daftar belanja: JTextField, tombol Tambah, JList, dan tombol Hapus terpilih.
 */
class DaftarBelanjaPanel extends JPanel {

    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> daftar = new JList<>(model);
    private final JTextField txtBarang = new JTextField(22);
    private final JLabel lblJumlah = new JLabel();

    public DaftarBelanjaPanel() {
        super(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JButton btnTambah = new JButton("Tambah");
        JButton btnHapus = new JButton("Hapus terpilih");

        JPanel atas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        atas.add(new JLabel("Barang:"));
        atas.add(txtBarang);
        atas.add(btnTambah);

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.add(lblJumlah, BorderLayout.WEST);
        JPanel kanan = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        kanan.add(btnHapus);
        bawah.add(kanan, BorderLayout.EAST);

        JScrollPane gulir = new JScrollPane(daftar);
        gulir.setBorder(BorderFactory.createTitledBorder("Daftar Belanja"));

        add(atas, BorderLayout.NORTH);
        add(gulir, BorderLayout.CENTER);
        add(bawah, BorderLayout.SOUTH);

        btnTambah.addActionListener(e -> tambah());
        txtBarang.addActionListener(e -> tambah());
        btnHapus.addActionListener(e -> hapus());
        perbaruiJumlah();
    }

    private void tambah() {
        String barang = txtBarang.getText().trim();
        if (barang.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama barang tidak boleh kosong.",
                    "Daftar Belanja", JOptionPane.WARNING_MESSAGE);
            return;
        }
        model.addElement(barang);
        txtBarang.setText("");
        txtBarang.requestFocusInWindow();
        perbaruiJumlah();
    }

    private void hapus() {
        int[] terpilih = daftar.getSelectedIndices();
        if (terpilih.length == 0) {
            JOptionPane.showMessageDialog(this, "Pilih barang yang ingin dihapus.",
                    "Daftar Belanja", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        // hapus dari indeks terbesar agar indeks lain tidak bergeser
        for (int i = terpilih.length - 1; i >= 0; i--) {
            model.remove(terpilih[i]);
        }
        perbaruiJumlah();
    }

    private void perbaruiJumlah() {
        lblJumlah.setText("Total barang: " + model.getSize());
    }
}

// ============================================================
/**
 * Tugas 2 no. 4 - Jam digital dengan javax.swing.Timer (update tiap 1 detik).
 */
class JamDigitalPanel extends JPanel {

    private static final DateTimeFormatter FORMAT_JAM = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter FORMAT_TANGGAL =
            DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.forLanguageTag("id-ID"));

    private final JLabel lblJam = new JLabel("00:00:00", SwingConstants.CENTER);
    private final JLabel lblTanggal = new JLabel(" ", SwingConstants.CENTER);
    private final javax.swing.Timer timer;

    public JamDigitalPanel() {
        super(new GridBagLayout());
        setBackground(Color.BLACK);

        lblJam.setFont(new Font(Font.MONOSPACED, Font.BOLD, 84));
        lblJam.setForeground(new Color(0x39FF14));
        lblTanggal.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 22));
        lblTanggal.setForeground(Color.LIGHT_GRAY);
        JLabel lblKet = new JLabel("Dibuat dengan javax.swing.Timer", SwingConstants.CENTER);
        lblKet.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));
        lblKet.setForeground(Color.GRAY);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.insets = new Insets(4, 4, 4, 4);
        g.gridy = 0;
        add(lblJam, g);
        g.gridy = 1;
        add(lblTanggal, g);
        g.gridy = 2;
        g.insets = new Insets(24, 4, 4, 4);
        add(lblKet, g);

        perbarui();
        timer = new javax.swing.Timer(1000, e -> perbarui());
        timer.setInitialDelay(0);
        timer.start();
    }

    private void perbarui() {
        LocalDateTime sekarang = LocalDateTime.now();
        lblJam.setText(sekarang.format(FORMAT_JAM));
        lblTanggal.setText(sekarang.format(FORMAT_TANGGAL));
    }
}