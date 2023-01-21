pub enum EguiEvents {
    SizeChanged(u32, u32),
    MouseDown(egui::PointerButton),
    MouseUp(egui::PointerButton),
    MouseMotion(i32, i32),
    KeyDown(egui::Key),
    KeyUp(egui::Key),
    TextInput(String),
    MouseWheel(i32, i32),
}