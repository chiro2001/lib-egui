use crate::painter::{EguiPainter, MeshPainterHandler};
use egui::{Event, Mesh};
use crate::state::EguiStateHandler;
use crate::utils::egui_cast;

mod basic;
pub mod events;
pub mod painter;
pub mod state;
mod utils;

#[derive(Debug)]
pub struct Egui {
    pub painter: EguiPainter,
    pub state: EguiStateHandler,
}

impl Egui {
    pub fn new(handler: MeshPainterHandler) -> Self {
        Self {
            painter: EguiPainter::new(handler),
            state: Default::default(),
        }
    }
}

#[no_mangle]
pub extern "C" fn egui_create(handler: *const ()) -> *const Egui {
    println!("creating(handler: {:?})...", handler);
    let handler: MeshPainterHandler = unsafe { std::mem::transmute(handler) };
    // let e = &Egui::new(handler);
    let e = Box::new(Egui::new(handler));
    let e = Box::leak(e);
    println!("return instance at: {:?}", e as *const Egui);
    e
}

#[no_mangle]
pub unsafe extern "C" fn egui_run(g: *mut Egui) {
    println!("running(g: {:?})...", g);
    let e = egui_cast(g);
    println!("painter: {:?}", e.painter);
    e.painter.paint_mesh(&Mesh::default());
}
