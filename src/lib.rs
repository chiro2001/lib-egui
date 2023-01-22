use std::fmt::Debug;
use std::thread::sleep;
use std::time::Duration;
use egui::Mesh;
use crate::painter::{EguiPainter, MeshPainterHandler};
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

async fn egui_running(ui: &Egui) {
    loop {
        println!("egui thread running");
        sleep(Duration::from_millis(300));
    }
}

#[no_mangle]
pub unsafe extern "C" fn egui_run(g: *mut Egui) {
    let ui = egui_cast(g);
    std::thread::spawn(move || futures::executor::block_on(egui_running(ui)));
}

#[no_mangle]
pub unsafe extern "C" fn egui_run_block(g: *mut Egui) {
    let ui = egui_cast(g);
    futures::executor::block_on(egui_running(ui));
}
