use egui::epaint::Vertex;
use egui::{Mesh, TextureId};

pub type MeshPainterHandler = *const fn(
    indices: *const u32,
    indices_len: usize,
    // vertices: *const u32,
    vertices: *const Vertex,
    vertices_len: usize,
    texture_managed: bool,
    texture_id: u64,
) -> ();

pub struct Painter {
    pub(crate) handler: MeshPainterHandler,
}

impl Painter {
    pub fn new(handler: MeshPainterHandler) -> Self {
        Self { handler }
    }
    pub fn paint_mesh(&self, mesh: &Mesh) {
        let indices_len = mesh.indices.len();
        let vertices_len = mesh.vertices.len();
        let texture_managed = match mesh.texture_id {
            TextureId::Managed(_) => true,
            TextureId::User(_) => false,
        };
        let texture_id = match mesh.texture_id {
            TextureId::Managed(id) => id,
            TextureId::User(id) => id,
        };
        unsafe {
            self.handler.read()(
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