use crate::painter::{EguiPainter, MeshPainterHandler};
use egui::{Event, Mesh};

pub mod painter;
pub mod state;

#[no_mangle]
pub extern "C" fn test(left: usize, right: usize) -> usize {
    left + right
}

#[derive(Debug)]
pub struct Egui {
    pub painter: EguiPainter,
    pub events: Vec<Event>,
}

impl Egui {
    pub fn new(handler: MeshPainterHandler) -> Self {
        Self {
            painter: EguiPainter::new(handler),
            events: vec![],
        }
    }
}

#[no_mangle]
pub extern "C" fn egui_create(handler: MeshPainterHandler) -> *const Egui {
    println!("creating(handler: {:?})...", handler);
    // let e = &Egui::new(handler);
    let e = Box::new(Egui::new(handler));
    let e = Box::leak(e);
    println!("return instance at: {:?}", e as *const Egui);
    let handler = e.painter.handler;
    println!("handler: {:?}", handler);
    e
}

#[no_mangle]
pub unsafe extern "C" fn egui_run(g: *const Egui) {
    println!("running(g: {:?})...", g);
    let e = &*g;
    println!("painter: {:?}", e.painter);
    let handler = e.painter.handler;
    e.painter.paint_mesh(&Mesh::default());
    println!("handler: {:?}", handler);
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn it_works() {
        let result = test(2, 2);
        assert_eq!(result, 4);
    }

    #[test]
    fn create_run_egui() {
        // let g = egui_create(0xff as MeshPainterHandler);
        // unsafe { egui_run(g) };
        // let g = Egui::new(0xaa as MeshPainterHandler);
        // unsafe { egui_run(&g) };
    }
}
