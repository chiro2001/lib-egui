use egui::epaint::{Primitive, Vertex};
use egui::{ClippedPrimitive, TextureId};
use tracing::info;

pub type PainterHandler = extern "C" fn(
    min_x: f32,
    min_y: f32,
    max_x: f32,
    max_y: f32,
    indices: *const u32,
    indices_len: usize,
    vertices: *const Vertex,
    vertices_len: usize,
    texture_managed: bool,
    texture_id: u64,
) -> ();

#[derive(Debug)]
pub struct EguiPainter {
    pub(crate) handler: PainterHandler,
    pub(crate) pixels_per_point: f32,
}

impl EguiPainter {
    pub fn new(handler: PainterHandler) -> Self {
        Self {
            handler,
            pixels_per_point: 1.0,
        }
    }
    pub fn paint_primitive(&self, primitive: &ClippedPrimitive) {
        if let Primitive::Mesh(mesh) = &primitive.primitive {
            let indices_len = mesh.indices.len();
            let vertices_len = mesh.vertices.len();
            // if vertices_len > 1 {
            //     let first = mesh.vertices.as_ptr() as u64;
            //     let second = mesh.vertices.get(1).unwrap() as *const Vertex as u64;
            //     let offset = second - first;
            //     info!("pointer vertex += {}", offset);
            // }
            let texture_managed = match mesh.texture_id {
                TextureId::Managed(_) => true,
                TextureId::User(_) => false,
            };
            let texture_id = match mesh.texture_id {
                TextureId::Managed(id) => id,
                TextureId::User(id) => id,
            };
            let rect = primitive.clip_rect;
            // info!(
            //     "vertex: [{:x}]{:?}, [{:x}]{:?}",
            //     mesh.vertices.get(0).unwrap() as *const Vertex as u64,
            //     mesh.vertices.get(0).unwrap().pos,
            //     mesh.vertices.get(1).unwrap() as *const Vertex as u64,
            //     mesh.vertices.get(1).unwrap().pos
            // );
            (self.handler)(
                rect.min.x,
                rect.min.y,
                rect.max.x,
                rect.max.y,
                mesh.indices.as_ptr(),
                indices_len,
                mesh.vertices.as_ptr(),
                vertices_len,
                texture_managed,
                texture_id,
            );
        }
    }
}
