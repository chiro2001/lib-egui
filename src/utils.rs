use crate::Egui;

pub fn egui_cast(g: *mut Egui) -> &'static mut Egui {
    unsafe { &mut *g }
}