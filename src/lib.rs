use crate::painter::{EguiPainter, MeshPainterHandler};
use egui::{Event, Mesh};

pub mod events;
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

#[no_mangle]
pub extern "C" fn egui_create(handler: MeshPainterHandler) -> *const Egui {
    println!("creating(handler: {:?})...", handler);
    let e = &Egui {
        painter: EguiPainter { handler },
        events: vec![],
    };
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
    // e.painter.paint_mesh(&Mesh::default());
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
}
